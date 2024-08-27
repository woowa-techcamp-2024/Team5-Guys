package info.logbat.domain.log.queue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class LogQueue<T> implements Producer<T>, Consumer<T> {

    private static final VarHandle CONSUMER_THREADS;

    static {
        try {
            CONSUMER_THREADS = MethodHandles.lookup()
                .findVarHandle(LogQueue.class, "consumerThread", Thread.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final LinkedList<T> queue = new LinkedList<>();
    private final long timeoutNanos;
    private final int bulkSize;
    private volatile Thread consumerThread;


    public LogQueue(@Value("${jdbc.async.timeout}") Long timeoutMillis,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize) {
        this.timeoutNanos = timeoutMillis * 1_000_000; // Convert to nanoseconds
        this.bulkSize = bulkSize;
    }

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

    @Override
    public void produce(List<T> data) {
        queue.addAll(data);
        if (consumerThread != null && queue.size() >= bulkSize) {
            LockSupport.unpark(consumerThread);
        }
    }
}