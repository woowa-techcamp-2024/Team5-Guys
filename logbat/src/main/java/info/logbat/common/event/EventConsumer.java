package info.logbat.common.event;

import java.util.List;

public interface EventConsumer<T> {

    List<T> consume();
}
