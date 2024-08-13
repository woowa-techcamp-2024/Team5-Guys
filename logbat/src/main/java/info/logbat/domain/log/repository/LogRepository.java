package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class LogRepository {

  private final JdbcTemplate jdbcTemplate;

  public long save(Log log) {
    String sql = "INSERT INTO logs (application_id, level, log_data, timestamp) VALUES (?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, log.getApplicationId());
      ps.setString(2, log.getLevel().name());
      ps.setString(3, log.getLogData().getValue());
      ps.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
      return ps;
    }, keyHolder);

    if (keyHolder.getKey() == null) {
      throw new IllegalStateException("로그 저장에 실패했습니다.");
    }

    return keyHolder.getKey().longValue();
  }

  public Optional<Log> findById(Long logId) {
    String sql = "SELECT * FROM logs WHERE log_id = ?";

    Log log = jdbcTemplate.queryForObject(sql, logRowMapper(), logId);

    return Optional.ofNullable(log);
  }

  private RowMapper<Log> logRowMapper() {
    return (rs, rowNum) -> new Log(
        rs.getLong("log_id"),
        rs.getLong("application_id"),
        rs.getString("level"),
        rs.getString("log_data"),
        rs.getTimestamp("timestamp").toLocalDateTime()
    );
  }
}
