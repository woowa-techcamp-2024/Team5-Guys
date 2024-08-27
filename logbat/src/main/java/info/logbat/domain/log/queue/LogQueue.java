package info.logbat.domain.log.queue;

import info.logbat.common.event.EventConsumer;
import info.logbat.common.event.EventProducer;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 로깅 데이터 전달 목적의 Thread-Safe Queue 구현체로, 생산과 소비 작업을 모두 지원합니다. 이 클래스는 단일 스레드 환경에서 작동하도록 설계되었으며, 효율적인
 * 대량 작업을 허용합니다.
 * <p>
 * 이 큐는 생산자가 데이터를 추가하고, 소비자가 데이터를 꺼내는 방식으로 동작합니다. 소비자는 큐에 충분한 데이터가 쌓일 때까지 대기하다가, 큐에 데이터가 충분히 쌓이면
 * 일괄적으로 데이터를 꺼내서 처리합니다. 이 때, 일괄 처리 크기는 생성자를 통해 지정할 수 있습니다. 만약 큐에 충분한 데이터가 쌓이지 않은 상태에서 소비자가 데이터를 꺼내려
 * 할 때, 소비자 스레드는 일정 시간 동안 대기하다가 큐에 데이터가 추가되면 깨어나서 데이터를 꺼냅니다. 이 때 대기 시간은 생성자를 통해 지정할 수 있습니다.
 * </p>
 *
 * @param <T> 이 큐에 저장되는 요소의 타입
 */
@Primary
@Component
public class LogQueue<T> implements EventProducer<T>, EventConsumer<T> {

    //consumerThread 필드에 대한 원자적 연산을 위한 VarHandle
    private static final VarHandle CONSUMER_THREADS;

    static {
        try {
            CONSUMER_THREADS = MethodHandles.lookup()
                .findVarHandle(LogQueue.class, "consumerThread", Thread.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // T 타입의 요소를 저장하는 list
    private final LinkedList<T> queue;
    // 소비자 스레드가 대기하는 시간 (나노초 단위)
    private final long timeoutNanos;
    // 일괄 처리 크기
    private final int bulkSize;
    // 소비자 스레드, volatile로 선언하여 가시성 보장
    private volatile Thread consumerThread;


    /**
     * 지정된 타임아웃과 벌크 크기로 새 LogQueue를 생성합니다.
     *
     * @param timeoutMillis 큐가 비어있을 때 소비자가 대기하는 시간(밀리초)
     * @param bulkSize      단일 작업에서 소비될 수 있는 최대 요소 수
     */
    public LogQueue(@Value("${jdbc.async.timeout}") Long timeoutMillis,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize) {
        this.queue = new LinkedList<>();
        this.timeoutNanos = timeoutMillis * 1_000_000; // Convert to nanoseconds
        this.bulkSize = bulkSize;
    }

    /**
     * 큐에서 요소를 일괄적으로 꺼내서 반환합니다. 큐에 충분한 요소가 쌓이지 않은 경우, 소비자 스레드는 일정 시간 동안 대기합니다.
     * <p>
     * 이 메서드는 단일 스레드 환경에서만 호출해야 합니다. 만약 다중 스레드 환경에서 호출하면 예상치 못한 결과가 발생할 수 있습니다. 이 메서드는 큐에 충분한 요소가 쌓일
     * 때까지 대기하다가, 큐에 요소가 쌓이면 일괄적으로 요소를 꺼내서 반환합니다. 만약 큐에 충분한 요소가 쌓이지 않은 경우, 일정 시간 동안 대기하다가 큐에 요소가
     * 추가되면 깨어나서 요소를 꺼냅니다. 이 때 대기 시간은 생성자를 통해 지정할 수 있습니다. 이 메서드는 큐에 쌓인 요소를 꺼내서 반환하는 것이 목적이므로, 큐에 요소를
     * 추가하는 작업은 {@link #produce(List)} 메서드를 사용해야 합니다.
     * </p>
     *
     * @return 큐에서 꺼낸 요소의 리스트 (최대 {@link #bulkSize}개)
     */
    @Override
    public List<T> consume() {
        List<T> result = new ArrayList<>(bulkSize);

        if (queue.size() >= bulkSize) {
            for (int i = 0; i < bulkSize; i++) {
                result.add(queue.poll());
            }
            return result;
        }

        Thread current = Thread.currentThread();
        CONSUMER_THREADS.set(this, current);

        do {
            LockSupport.parkNanos(timeoutNanos);
        } while (queue.isEmpty());

        for (int i = 0; i < bulkSize; i++) {
            result.add(queue.poll());
            if (queue.isEmpty()) {
                break;
            }
        }

        CONSUMER_THREADS.weakCompareAndSet(this, current, null);
        return result;
    }

    /**
     * 큐에 요소를 추가합니다. 큐에 요소가 추가되면, 소비자 스레드를 깨워서 요소를 꺼내도록 합니다.
     * <p>
     * 이 메서드는 단일 스레드 환경에서만 호출해야 합니다. 만약 다중 스레드 환경에서 호출하면 예상치 못한 결과가 발생할 수 있습니다. 이 메서드는 큐에 요소를 추가하는
     * 것이 목적이므로, 큐에서 요소를 꺼내는 작업은 {@link #consume()} 메서드를 사용해야 합니다.
     * </p>
     *
     * @param data 큐에 추가할 요소의 리스트
     */
    @Override
    public void produce(List<T> data) {
        queue.addAll(data);
        if (consumerThread != null && queue.size() >= bulkSize) {
            LockSupport.unpark(consumerThread);
        }
    }
}