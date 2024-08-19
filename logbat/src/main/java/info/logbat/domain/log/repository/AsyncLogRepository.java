package info.logbat.domain.log.repository;

import info.logbat.common.util.UUIDUtil;
import info.logbat.domain.log.domain.Log;
import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class AsyncLogRepository implements LogRepository {

  private final JdbcTemplate jdbcTemplate;
  private final LinkedBlockingQueue<Log> logQueue = new LinkedBlockingQueue<>();

  private final ExecutorService leaderExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService followerExecutor = Executors.newFixedThreadPool(20);

  private static final Long DEFAULT_RETURNS = 0L;
  private static final Long DEFAULT_TIMEOUT = 2000L;
  private static final Integer DEFAULT_BULK_SIZE = 100;

  @PostConstruct
  public void init() {
    log.info("AsyncLogRepository is initialized.");
    leaderExecutor.execute(this::leaderTask);
  }

  @Override
  public long save(Log log) {
    logQueue.add(log);
    return DEFAULT_RETURNS;
  }

  @Override
  public Optional<Log> findById(Long logId) {
    String sql = "SELECT * FROM logs WHERE log_id = ?";

    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              sql,
              LOG_ROW_MAPPER,
              logId
          ));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  private void leaderTask() {
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
        followerExecutor.execute(() -> saveLogsToDatabase(logs));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("Leader thread was interrupted. Exiting.", e);
        break;
      } catch (Exception e) {
        log.error("Unexpected error in leader thread", e);
      }
    }
  }

  private void saveLogsToDatabase(List<Log> logs) {
    if (logs.isEmpty()) {
      return;
    }

    StringBuilder sql = new StringBuilder(
        "INSERT INTO logs (app_key, level, data, timestamp) VALUES ");

    for (int i = 0; i < logs.size(); i++) {
      sql.append("(?, ?, ?, ?)");
      if (i < logs.size() - 1) { // 마지막 요소가 아닌 경우
        sql.append(", ");
      }
    }

    jdbcTemplate.update(sql.toString(), ps -> {
      int paramIndex = 1;
      for (Log log : logs) {
        ps.setBytes(paramIndex++, UUIDUtil.uuidStringToBytes(log.getAppKey()));
        ps.setInt(paramIndex++, log.getLevel().ordinal());
        ps.setString(paramIndex++, log.getData().getValue());
        ps.setTimestamp(paramIndex++, Timestamp.valueOf(log.getTimestamp()));
      }
    });
  }

  private static final RowMapper<Log> LOG_ROW_MAPPER = (rs, rowNum) -> new Log(
      rs.getLong("log_id"),
      UUIDUtil.bytesToUuidString(rs.getBytes("app_key")),
      rs.getInt("level"),
      rs.getString("data"),
      rs.getTimestamp("timestamp").toLocalDateTime()
  );

}
