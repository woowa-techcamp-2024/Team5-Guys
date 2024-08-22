package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
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
public class SynchronousLogRepository implements LogRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public long save(Log log) {
        String insertSql = "INSERT INTO logs (app_id, level, data, timestamp) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql,
                Statement.RETURN_GENERATED_KEYS);
            extractedPreparedStatement(log, ps);
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey())
            .map(Number::longValue)
            .orElseThrow(() -> new IllegalStateException("로그 저장에 실패했습니다."));
    }

    @Override
    public List<Log> saveAll(List<Log> logs) {
        String insertSql = "INSERT INTO logs (app_id, level, data, timestamp) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(insertSql, logs, logs.size(),
            (ps, log) -> extractedPreparedStatement(log, ps));
        return logs;
    }

    @Override
    public Optional<Log> findById(Long id) {
        String selectSql = "SELECT * FROM logs WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(selectSql, logRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private void extractedPreparedStatement(Log log, PreparedStatement ps) throws SQLException {
        ps.setLong(1, log.getAppId());
        ps.setInt(2, log.getLevel().ordinal());
        ps.setString(3, log.getData().getValue());
        ps.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
    }

    private final RowMapper<Log> logRowMapper = (rs, rowNum) -> new Log(rs.getLong("id"),
        rs.getLong("app_id"),
        rs.getInt("level"),
        rs.getString("data"),
        rs.getTimestamp("timestamp").toLocalDateTime()
    );
}
