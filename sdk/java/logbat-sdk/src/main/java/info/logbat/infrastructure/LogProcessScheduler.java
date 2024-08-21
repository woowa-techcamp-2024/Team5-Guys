package info.logbat.infrastructure;

import info.logbat.infrastructure.payload.LogSendRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LogProcessScheduler {

    private static final Integer DEFAULT_BULK_SIZE = 100;
    private static final Integer DEFAULT_INTERVAL = 2000;

    private final Consumer<List<LogSendRequest>> sendFunction;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * LogProcessScheduler는 주기적으로 LogBuffer에 저장된 로그를 읽어와 전송하는 스케줄러입니다. logBuffer에 로그가 없을 경우 아무 동작도
     * 하지 않습니다.
     *
     * @param sendFunction
     * @param logBuffer
     */
    public LogProcessScheduler(Consumer<List<LogSendRequest>> sendFunction, LogBuffer logBuffer) {
        this.sendFunction = sendFunction;
        scheduler.scheduleAtFixedRate(
            () -> {
                // catch block에서 logs를 잡아 retryQueue에 넣는 로직을 구현하기 위해 try catch block 이전에 선언했습니다.
                List<LogSendRequest> logs = new ArrayList<>();
                try {
                    logs.addAll(logBuffer.getLogs(DEFAULT_BULK_SIZE));
                    if (logs.isEmpty()) {
                        return;
                    }
                    this.sendFunction.accept(logs);
                } catch (Throwable e) {
                    // TODO retryQueue에 저장하고 재처리 하도록 구현 필요함
                    e.printStackTrace();
                }
            },
            0,
            DEFAULT_INTERVAL,
            TimeUnit.MILLISECONDS
        );
    }
}
