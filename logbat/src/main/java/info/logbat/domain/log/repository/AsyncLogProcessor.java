package info.logbat.domain.log.repository;

import com.zaxxer.hikari.HikariDataSource;
import info.logbat.domain.log.domain.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * 비동기식 로그 처리를 담당하는 것은 AsyncLogProcessor입니다. 리더-팔로워 패턴을 사용하여 로그 항목을 처리하고 대량으로 저장합니다. 리더는 로그 큐에서 로그
 * 항목을 가져와 팔로워 스레드 풀에 전달합니다. 팔로워는 로그 항목을 대량으로 저장합니다.
 */
@Slf4j
@Component
public class AsyncLogProcessor {

    private final LinkedBlockingQueue<Log> logQueue = new LinkedBlockingQueue<>();
    private final ExecutorService followerExecutor;
    private final long defaultTimeout;
    private final int defaultBulkSize;

    /**
     * 지정된 시간 제한, 일괄 크기 및 JdbcTemplate을 사용하여 AsyncLogProcessor를 구축합니다.
     *
     * @param timeout      팔로워 스레드가 대기하는 시간 제한
     * @param bulkSize     팔로워 스레드가 한 번에 처리하는 로그 항목 수
     * @param jdbcTemplate JdbcTemplate
     */
    public AsyncLogProcessor(@Value("${jdbc.async.timeout}") Long timeout,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize,
        JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (!(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("DataSource is null");
        }
        int poolSize = ((HikariDataSource) dataSource).getMaximumPoolSize();
        log.debug("Creating AsyncLogProcessor with pool size: {}", poolSize);

        // use 50% of the pool size for the follower thread pool
        this.followerExecutor = Executors.newFixedThreadPool(poolSize * 5 / 10);
        this.defaultTimeout = Objects.requireNonNullElse(timeout, 2000L);
        this.defaultBulkSize = Objects.requireNonNullElse(bulkSize, 100);
    }

    /**
     * 비동기식 로그 처리를 시작합니다. 리더 스레드를 시작하고 로그 저장 함수를 전달합니다.
     *
     * @param saveFunction 로그 저장 함수
     */
    public void init(Consumer<List<Log>> saveFunction) {
        CompletableFuture.runAsync(() -> leaderTask(saveFunction));
    }

    /**
     * 로그를 제출합니다.
     * <p>
     * 로그 큐에 로그를 추가합니다.
     *
     * @param log 로그
     */
    public void submitLog(Log log) {
        // Queue 크기를 제한할 거면 offer 사용하도록 변경
        logQueue.add(log);
    }

    /**
     * 로그 리스트를 제출합니다.
     *
     * @param logs 로그 리스트
     */
    public void submitLogs(List<Log> logs) {
        logQueue.addAll(logs);
    }

    /**
     * 리더 스레드를 시작합니다. 로그를 저장하는 함수를 전달합니다.
     * <p>
     * 리더 스레드는 로그 큐에서 로그 항목을 가져와 팔로워 스레드 풀에 전달합니다. 팔로워는 로그 항목을 대량으로 저장합니다. 리더 스레드는 종료되지 않는 한 계속해서
     * 로그를 처리합니다. 리더 스레드가 종료되면 팔로워 스레드 풀도 종료됩니다.
     *
     * @param saveFunction 로그 저장 함수
     */
    private void leaderTask(Consumer<List<Log>> saveFunction) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Log log = logQueue.poll(defaultTimeout, TimeUnit.MILLISECONDS);
                /*
                 * Log가 천천히 들어오는 경우 Timeout에 한 번씩 저장
                 *
                 * Log가 높은 부하로 들어오는 경우 Bulk Size만큼 한 번에 저장
                 *
                 * Timeout동안 들어온 Log가 없는 경우 다음 반복문 cycle 수행
                 */
                if (log == null) {
                    continue;
                }
                List<Log> logs = new ArrayList<>();
                logs.add(log);
                //drainTo는 Queue에 있는 요소를 maxElements만큼 꺼내서 Collection에 담아준다.
                logQueue.drainTo(logs, defaultBulkSize - 1);
                // Follower Thread Pool에 저장 요청
                followerExecutor.execute(() -> saveFunction.accept(logs));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Leader thread was interrupted. Exiting.", e);
                break;
            } catch (Exception e) {
                log.error("Unexpected error in leader thread", e);
            }
        }
    }
}