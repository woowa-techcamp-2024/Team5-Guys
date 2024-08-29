package info.logbat.domain.log.repository;

import com.zaxxer.hikari.HikariDataSource;
import info.logbat.common.event.EventConsumer;
import info.logbat.domain.log.domain.Log;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 비동기적으로 로그를 처리하는 클래스입니다. 이 클래스는 로그를 저장하는 비동기 작업을 수행하며, 이를 위해 별도의 스레드 풀을 사용합니다.
 */
@Slf4j
public class AsyncLogProcessor {

    // 로그 저장 작업을 수행하는 스레드 풀
    private final ExecutorService followerExecutor;
    // 로그를 소비하는 EventConsumer 객체
    private final EventConsumer<Log> eventConsumer;

    /**
     * 지정된 Consumer 객체와 JdbcTemplate을 사용하여 새 AsyncLogProcessor를 생성합니다. HikariDataSource의 최대 풀 크기의
     * 50%를 사용하여 스레드 풀을 초기화합니다.
     *
     * @param eventConsumer 로그를 소비하는 Consumer 객체
     * @param jdbcTemplate  JdbcTemplate 객체
     */
    public AsyncLogProcessor(EventConsumer<Log> eventConsumer,
        JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (!(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("DataSource is null");
        }
        int poolSize = ((HikariDataSource) dataSource).getMaximumPoolSize();
        log.debug("Creating AsyncLogProcessor with pool size: {}", poolSize);

        this.eventConsumer = eventConsumer;
        // use 50% of the pool size for the follower thread pool
        this.followerExecutor = Executors.newFixedThreadPool(poolSize * 5 / 10);
    }

    /**
     * 로그 처리를 초기화하고 시작합니다. 리더 태스크를 비동기적으로 실행합니다.
     *
     * @param saveFunction 로그를 저장하는 함수
     */
    public void init(Consumer<List<Log>> saveFunction) {
        CompletableFuture.runAsync(() -> leaderTask(saveFunction));
    }

    /**
     * 리더 태스크를 실행하는 private 메서드입니다. 로그를 소비하고 팔로워 스레드 풀에 저장 작업을 제출합니다.
     *
     * @param saveFunction 로그를 저장하는 함수
     */
    private void leaderTask(Consumer<List<Log>> saveFunction) {
        while (!Thread.currentThread().isInterrupted()) {
            List<Log> logs = eventConsumer.consume();
            followerExecutor.execute(() -> saveFunction.accept(logs));
        }
    }

    @Deprecated(forRemoval = true)
    public void submitLog(Log log) {
        throw new UnsupportedOperationException("This method is deprecated");
    }

    @Deprecated(forRemoval = true)
    public void submitLogs(List<Log> logs) {
        throw new UnsupportedOperationException("This method is deprecated");
    }
}