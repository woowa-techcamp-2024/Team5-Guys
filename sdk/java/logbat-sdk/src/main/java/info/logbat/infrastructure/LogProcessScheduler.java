package info.logbat.infrastructure;

import info.logbat.infrastructure.payload.LogSendRequest;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LogProcessScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Integer DEFAULT_BULK_SIZE = 100;
    private static final Integer DEFAULT_INTERVAL = 2000;

    public LogProcessScheduler(Consumer<List<LogSendRequest>> sendFunction, LogBuffer logBuffer) {
        scheduler.scheduleAtFixedRate(
            () -> sendFunction.accept(logBuffer.getLogs(DEFAULT_BULK_SIZE)),
            0,
            DEFAULT_INTERVAL,
            TimeUnit.MILLISECONDS
        );
    }
}
