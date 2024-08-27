package info.logbat.domain.log.queue;

import java.util.List;

public interface Producer<T> {

    void produce(List<T> data);

}
