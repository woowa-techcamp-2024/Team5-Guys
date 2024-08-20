package info.logbat.domain.log.repository;

import com.zaxxer.hikari.HikariDataSource;
import info.logbat.domain.log.domain.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
public class AsyncLogProcessor {

    private final LinkedBlockingQueue<Log> logQueue = new LinkedBlockingQueue<>();
    private final ExecutorService leaderExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService followerExecutor;

    private static final long DEFAULT_TIMEOUT = 2000L;
    private static final int DEFAULT_BULK_SIZE = 100;

    public AsyncLogProcessor(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null || !(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("DataSource is null");
        }
        int poolSize = ((HikariDataSource) dataSource).getMaximumPoolSize();

        // use 50% of the pool size for the follower thread pool
        followerExecutor = Executors.newFixedThreadPool(poolSize * 5 / 10);
    }

    public void init(Consumer<List<Log>> saveFunction) {
        leaderExecutor.execute(() -> leaderTask(saveFunction));
    }

    public void submitLog(Log log) {
        // Queue 크기를 제한할 거면 offer 사용하도록 변경
        logQueue.add(log);
    }

    private void leaderTask(Consumer<List<Log>> saveFunction) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Log log = logQueue.poll(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                /**
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
                /**
                 * drainTo는 Queue에 있는 요소를 maxElements만큼 꺼내서 Collection에 담아준다.
                 */
                logQueue.drainTo(logs, DEFAULT_BULK_SIZE - 1);

                /**
                 * Follower Thread Pool에 저장 요청
                 */
                // TODO 저장에 실패했을 때의 추가 로직이 필요합니다.
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