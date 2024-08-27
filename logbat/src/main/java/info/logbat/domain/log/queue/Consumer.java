package info.logbat.domain.log.queue;

import java.util.List;

public interface Consumer<T> {

    List<T> consume();
}
