package info.logbat.common.event;

import java.util.List;

public interface EventProducer<T> {

    void produce(List<T> data);
}
