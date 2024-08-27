package info.logbat.domain.log.flatter;

import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.queue.Producer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogRequestFlatter {

    private final Producer<Log> producer;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public void flatten(List<Log> logs) {
        executor.submit(() -> producer.produce(logs));
    }
}
