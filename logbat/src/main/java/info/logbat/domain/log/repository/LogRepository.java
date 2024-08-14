package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LogRepository {

  private final JdbcTemplate jdbcTemplate;

  public long save(Log log) {
    String sql = "INSERT INTO logs (application_id, level, log_data, timestamp) VALUES (?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, log.getApplicationId());
      ps.setString(2, log.getLevel().name());
      ps.setString(3, log.getData().getValue());
      ps.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
      return ps;
    }, keyHolder);

    return Optional.ofNullable(keyHolder.getKey())
        .map(Number::longValue)
        .orElseThrow(() -> new IllegalStateException("로그 저장에 실패했습니다."));
  }

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

  private static final RowMapper<Log> LOG_ROW_MAPPER = (rs, rowNum) -> new Log(
      rs.getLong("log_id"),
      rs.getLong("application_id"),
      rs.getString("level"),
      rs.getString("log_data"),
      rs.getTimestamp("timestamp").toLocalDateTime()
  );
}
