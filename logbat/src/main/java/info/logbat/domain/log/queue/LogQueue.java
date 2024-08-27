package info.logbat.domain.log.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogQueue<T> implements Producer<T>, Consumer<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final long timeout;
    private final int bulkSize;

    private volatile Thread consumerThread;

    public LogQueue(@Value("${jdbc.async.timeout}") Long timeout,
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
            try {
                consumerThread = Thread.currentThread();
                consumerThread.sleep(timeout);
            } catch (InterruptedException e) {
                // ignore
            }
        } while (queue.isEmpty());

        // Bulk Size 만큼 꺼내서 반환
        for (int i = 0; i < bulkSize; i++) {
            result.add(queue.poll());
            if (queue.isEmpty()) {
                break;
            }
        }
        return result;
    }

    /*
     * Producer should be one thread
     */
    @Override
    public void produce(List<T> data) {
        queue.addAll(data);
        if (queue.size() >= bulkSize) {
            if (consumerThread != null) {
                consumerThread.interrupt();
            }
        }
    }
}
