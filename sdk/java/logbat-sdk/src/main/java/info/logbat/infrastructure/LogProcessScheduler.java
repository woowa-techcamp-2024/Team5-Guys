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

/**
 * LogProcessScheduler is responsible for periodically reading and sending logs stored in a
 * LogBuffer. It schedules log processing at fixed intervals and provides graceful shutdown
 * capabilities. If the logBuffer is empty, no action is taken during scheduled executions.
 * <p>
 * This class registers a shutdown hook to ensure graceful shutdown when the JVM terminates.
 *
 * @author MinJu Kim <a href="https://github.com/miiiinju1">GitHub</a>
 * @version 0.1.0
 * @see LogBuffer
 * @see LogSendRequest
 * @since 0.1.0
 */
public class LogProcessScheduler {

    private static final Integer DEFAULT_BULK_SIZE = 100;
    private static final Integer DEFAULT_INTERVAL = 2000;
    private static final Integer TERMINATION_TIMEOUT = 60;
    private static final Integer SHUTDOWN_THREAD_POOL_SIZE = 4; // Thread pool size for parallel processing

    private final Consumer<List<LogSendRequest>> sendFunction;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final LogBuffer logBuffer;

    /**
     * Constructs a new LogProcessScheduler.
     *
     * @param sendFunction A consumer function that sends a list of LogSendRequests.
     * @param logBuffer    The buffer containing logs to be processed.
     */
    public LogProcessScheduler(Consumer<List<LogSendRequest>> sendFunction, LogBuffer logBuffer) {
        this.sendFunction = sendFunction;
        this.logBuffer = logBuffer;
        startScheduling();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownSafely));
    }

    /**
     * Starts the scheduling of log processing tasks.
     */
    private void startScheduling() {
        scheduler.scheduleAtFixedRate(
            this::processLogs,
            0,
            DEFAULT_INTERVAL,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * Processes logs from the buffer. This method retrieves logs from the buffer and sends them
     * using the provided send function. If an exception occurs, it will be printed (TODO: implement
     * retry queue logic).
     */
    private void processLogs() {
        List<LogSendRequest> logs = new ArrayList<>();
        try {
            logs.addAll(logBuffer.getLogs(DEFAULT_BULK_SIZE));
            if (logs.isEmpty()) {
                return;
            }
            this.sendFunction.accept(logs);
        } catch (Throwable e) {
            // TODO: Implement storing in retry queue and reprocessing
            e.printStackTrace();
        }
    }

    /**
     * Performs a graceful shutdown of the scheduler and processes remaining logs. This method stops
     * the scheduler and sends all remaining logs in parallel before shutting down.
     *
     * @throws InterruptedException if the thread is interrupted during shutdown
     */
    public void gracefulShutdown() throws InterruptedException {
        // Shutdown the scheduler
        scheduler.shutdown();
        if (!scheduler.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS)) {
            scheduler.shutdownNow();
        }

        // Create a thread pool for processing remaining logs
        ExecutorService shutdownExecutor = Executors.newFixedThreadPool(SHUTDOWN_THREAD_POOL_SIZE);

        List<Callable<Void>> tasks = new ArrayList<>();
        List<LogSendRequest> logs;

        // Create tasks for remaining logs
        while (!(logs = logBuffer.getLogs(DEFAULT_BULK_SIZE)).isEmpty()) {
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
            // Execute all remaining log sending tasks in parallel and wait for completion
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
     * Safely shuts down the scheduler when called by the shutdown hook. This method catches and
     * handles InterruptedException during graceful shutdown.
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
