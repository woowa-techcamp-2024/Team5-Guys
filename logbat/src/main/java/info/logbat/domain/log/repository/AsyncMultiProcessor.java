package info.logbat.domain.log.repository;

import com.zaxxer.hikari.HikariDataSource;
import info.logbat.common.event.EventProducer;
import info.logbat.domain.log.queue.ReentrantLogQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
public class AsyncMultiProcessor<E> implements EventProducer<E> {

    private final List<ReentrantLogQueue<E>> queues;
    private final List<ExecutorService> flatterExecutors;
    private Consumer<List<E>> saveFunction;
    private final int queueCount;

    public AsyncMultiProcessor(@Value("${queue.count:3}") int queueCount,
        @Value("${jdbc.async.timeout:5000}") Long timeout,
        @Value("${jdbc.async.bulk-size:3000}") Integer bulkSize, JdbcTemplate jdbcTemplate) {
        this.queueCount = queueCount;
        this.queues = new ArrayList<>(queueCount);
        this.flatterExecutors = new ArrayList<>(queueCount);
        int poolSize = getPoolSize(jdbcTemplate);
        setup(queueCount, timeout, bulkSize, poolSize);
    }

    public void init(Consumer<List<E>> saveFunction) {
        this.saveFunction = saveFunction;
    }

    @Override
    public void produce(List<E> data) {
        if (data.isEmpty()) {
            return;
        }
        int selectedQueue = ThreadLocalRandom.current().nextInt(queueCount);
        flatterExecutors.get(selectedQueue).execute(() -> queues.get(selectedQueue).produce(data));
    }

    private void setup(int queueCount, Long timeout, Integer bulkSize, int poolSize) {
        ExecutorService followerExecutor = Executors.newFixedThreadPool(poolSize);
        ReentrantLogQueue<E> queue = new ReentrantLogQueue<>(timeout, bulkSize);

        for (int i = 0; i < queueCount; i++) {
            queues.add(queue);
            flatterExecutors.add(Executors.newSingleThreadExecutor());
        }
        CompletableFuture.runAsync(() -> leaderTask(queue, followerExecutor));
    }

    private void leaderTask(ReentrantLogQueue<E> queue, ExecutorService follower) {
        while (!Thread.currentThread().isInterrupted()) {
            List<E> element = queue.consume();
            follower.execute(() -> saveFunction.accept(element));
        }
    }

    private static int getPoolSize(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (!(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("DataSource is null");
        }
        int poolSize = ((HikariDataSource) dataSource).getMaximumPoolSize();
        log.debug("Creating AsyncLogProcessor with pool size: {}", poolSize);
        return poolSize * 5 / 10;
    }
}
