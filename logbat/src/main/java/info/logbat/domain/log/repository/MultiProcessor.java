package info.logbat.domain.log.repository;

import com.zaxxer.hikari.HikariDataSource;
import info.logbat.common.event.EventConsumer;
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

@Primary
@Slf4j
@Component
public class MultiProcessor<Log> implements EventProducer<Log>, EventConsumer<Log> {

    private final List<ReentrantLogQueue<Log>> queues;
    private final List<ExecutorService> followerExecutors;
    private Consumer<List<Log>> saveFunction;
    private final int queueCount;

    public MultiProcessor(@Value("${queue.count:5}") int queueCount,
        @Value("${jdbc.async.timeout}") Long timeout,
        @Value("${jdbc.async.bulk-size}") Integer bulkSize, JdbcTemplate jdbcTemplate) {

        this.queueCount = queueCount;
        this.queues = new ArrayList<>(queueCount);
        this.followerExecutors = new ArrayList<>(queueCount);

        DataSource dataSource = jdbcTemplate.getDataSource();
        if (!(dataSource instanceof HikariDataSource)) {
            throw new IllegalArgumentException("DataSource is null");
        }
        int poolSize = ((HikariDataSource) dataSource).getMaximumPoolSize();
        log.debug("Creating AsyncLogProcessor with pool size: {}", poolSize);
        ReentrantLogQueue<Log> queue = new ReentrantLogQueue<>(timeout, bulkSize);
        ExecutorService followerExecutor = Executors.newFixedThreadPool(poolSize * 5 / 10);
        CompletableFuture.runAsync(() -> leaderTask(queue, followerExecutor));
        for (int i = 0; i < queueCount; i++) {
            queues.add(queue);
        }
    }

    public void init(Consumer<List<Log>> saveFunction) {
        this.saveFunction = saveFunction;
    }

    private void leaderTask(ReentrantLogQueue<Log> queue, ExecutorService follower) {
        while (!Thread.currentThread().isInterrupted()) {
            List<Log> logs = queue.consume();
            follower.execute(() -> this.saveFunction.accept(logs));
        }
    }

    @Override
    public List<Log> consume() {
        int selectedQueue = ThreadLocalRandom.current().nextInt(queueCount);
        return queues.get(selectedQueue).consume();
    }

    @Override
    public void produce(List<Log> data) {
        if (data.isEmpty()) {
            return;
        }
        int selectedQueue = ThreadLocalRandom.current().nextInt(queueCount);
        queues.get(selectedQueue).produce(data);
    }
}
