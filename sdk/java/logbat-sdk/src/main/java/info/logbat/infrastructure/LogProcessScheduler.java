package info.logbat.infrastructure;

import info.logbat.infrastructure.payload.LogSendRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LogProcessScheduler {

    private static final Integer DEFAULT_BULK_SIZE = 100;
    private static final Integer DEFAULT_INTERVAL = 2000;
    private static final Integer TERMINATION_TIMEOUT = 60;
    private static final Integer SHUTDOWN_THREAD_POOL_SIZE = 4; // 병렬 처리를 위한 스레드 풀 크기

    private final Consumer<List<LogSendRequest>> sendFunction;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final LogBuffer logBuffer;


    /**
     * LogProcessScheduler는 주기적으로 LogBuffer에 저장된 로그를 읽어와 전송하는 스케줄러입니다. logBuffer에 로그가 없을 경우 아무 동작도
     * 하지 않습니다.
     * <p>
     * Shutdown hook을 등록하여 JVM 종료 시 gracefulShutdown을 호출합니다.
     *
     * @param sendFunction
     * @param logBuffer
     */
    public LogProcessScheduler(Consumer<List<LogSendRequest>> sendFunction, LogBuffer logBuffer) {
        this.sendFunction = sendFunction;
        this.logBuffer = logBuffer;
        startScheduling();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownSafely));
    }

    private void startScheduling() {
        scheduler.scheduleAtFixedRate(
            this::processLogs,
            0,
            DEFAULT_INTERVAL,
            TimeUnit.MILLISECONDS
        );
    }

    private void processLogs() {
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
    }

    /**
     * 스케줄러와 남아있는 로그들을 안전하게 종료하고 처리합니다. 현재 버퍼에 남아 있는 모든 로그를 병렬로 전송한 후 종료합니다.
     *
     * @throws InterruptedException 스레드가 인터럽트 될 경우 발생
     */
    public void gracefulShutdown() throws InterruptedException {
        // 스케줄러 종료
        scheduler.shutdown();
        if (!scheduler.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
            scheduler.shutdownNow();
        }

        // 남아있는 로그를 처리할 스레드 풀 생성
        ExecutorService shutdownExecutor = Executors.newFixedThreadPool(SHUTDOWN_THREAD_POOL_SIZE);

        List<Callable<Void>> tasks = new ArrayList<>();
        List<LogSendRequest> logs;

        // 로그 버퍼가 비어있지 않을 때까지 반복하여 작업 생성
        while (!(logs = logBuffer.getLogs(DEFAULT_BULK_SIZE)).isEmpty()) {
            // 람다 캡처링 때문에 final 변수로 선언
            final List<LogSendRequest> finalLogs = logs;
            tasks.add(() -> {
                try {
                    sendFunction.accept(finalLogs);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
        try {
            // 남아 있는 모든 로그 전송 작업을 병렬로 실행하고 완료될 때까지 기다림
            shutdownExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            shutdownExecutor.shutdown();
            if (!shutdownExecutor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
                shutdownExecutor.shutdownNow();
            }
        }
    }

    /**
     * Shutdown hook을 받아 gracefulShutdown을 호출합니다.
     */
    private void shutdownSafely() {
        try {
            gracefulShutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}