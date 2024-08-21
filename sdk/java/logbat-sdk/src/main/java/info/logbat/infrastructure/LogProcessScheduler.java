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

    /**
     * LogProcessScheduler는 주기적으로 LogBuffer에 저장된 로그를 읽어와 전송하는 스케줄러입니다. logBuffer에 로그가 없을 경우 아무 동작도
     * 하지 않습니다.
     *
     * @param sendFunction
     * @param logBuffer
     */
    public LogProcessScheduler(Consumer<List<LogSendRequest>> sendFunction, LogBuffer logBuffer) {
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    final List<LogSendRequest> logs = logBuffer.getLogs(DEFAULT_BULK_SIZE);
                    if (logs == null || logs.isEmpty()) {
                        return;
                    }
                    sendFunction.accept(logs);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            },
            0,
            DEFAULT_INTERVAL,
            TimeUnit.MILLISECONDS
        );
    }
}
