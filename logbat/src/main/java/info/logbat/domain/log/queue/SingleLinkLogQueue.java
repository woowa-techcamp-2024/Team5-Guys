package info.logbat.domain.log.queue;

import info.logbat.common.event.EventConsumer;
import info.logbat.common.event.EventProducer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

public class SingleLinkLogQueue<T> implements EventProducer<T>, EventConsumer<T> {

    private final SingleLinkedList<T> queue = new SingleLinkedList<>();
    private final long timeout;
    private final int bulkSize;

    public SingleLinkLogQueue(@Value("${jdbc.async.timeout}") Long timeout,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize) {
        this.timeout = timeout;
        this.bulkSize = bulkSize;
    }

    /*
     * Consumer should be one thread
     */
    @Override
    public List<T> consume() {
        List<T> result = new ArrayList<>(bulkSize);

        final long endTime = System.currentTimeMillis() + timeout;

        while (result.isEmpty()) {
            T data = queue.poll();
            if (data != null) {
                result.add(data);
            }
            if (result.size() >= bulkSize) {
                break;
            }
            if (System.currentTimeMillis() >= endTime) {
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
    }
}
