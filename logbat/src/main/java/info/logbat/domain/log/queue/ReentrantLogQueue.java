package info.logbat.domain.log.queue;

import info.logbat.common.event.EventConsumer;
import info.logbat.common.event.EventProducer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReentrantLogQueue<T> implements EventProducer<T>, EventConsumer<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final long timeout;
    private final int bulkSize;
    private final ReentrantLock bulkLock = new ReentrantLock();
    private final Condition bulkCondition = bulkLock.newCondition();

    public ReentrantLogQueue(@Value("${jdbc.async.timeout}") Long timeout,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize) {
        this.timeout = timeout;
        this.bulkSize = bulkSize;
    }

    /*
     * Consumer should be one thread
     */
    @Override
    public List<T> consume() {
        List<T> result = new ArrayList<>();

        try {
            bulkLock.lockInterruptibly();
            // Case1: Full Flush
            if (queue.size() >= bulkSize) {
                for (int i = 0; i < bulkSize; i++) {
                    result.add(queue.poll());
                }
                return result;
            }
            // Else Case: Blocking
            // Blocked while Queue is Not Empty
            do {
                bulkCondition.await(timeout, TimeUnit.MILLISECONDS);
            } while (queue.isEmpty());

            // Bulk Size 만큼 꺼내서 반환
            for (int i = 0; i < bulkSize; i++) {
                result.add(queue.poll());
                if (queue.isEmpty()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bulkLock.unlock();
        }
        return result;
    }

    /*
     * Producer should be one thread
     */
    @Override
    public void produce(List<T> data) {
        bulkLock.lock();
        try {
            queue.addAll(data);
            if (queue.size() >= bulkSize) {
                bulkCondition.signal();
            }
        } finally {
            bulkLock.unlock();
        }
    }

}
