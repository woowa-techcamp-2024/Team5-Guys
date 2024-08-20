package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import jakarta.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
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
    private final AsyncLogProcessor asyncLogProcessor;

    private static final Long DEFAULT_RETURNS = 0L;

    @PostConstruct
    public void init() {
        log.info("AsyncLogRepository is initialized.");
        asyncLogProcessor.init(this::saveLogsToDatabase);
    }

    @Override
    public long save(Log log) {
        asyncLogProcessor.submitLog(log);
        return DEFAULT_RETURNS;
    }

    @Override
    public Optional<Log> findById(Long logId) {
        String sql = "SELECT * FROM logs WHERE id = ?";

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

    private void saveLogsToDatabase(List<Log> logs) {
        if (logs.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder(
            "INSERT INTO logs (app_id, level, data, timestamp) VALUES ");

        for (int i = 0; i < logs.size(); i++) {
            sql.append("(?, ?, ?, ?)");
            if (i < logs.size() - 1) { // 마지막 요소가 아닌 경우에만 콤마 추가
                sql.append(", ");
            }
        }

        jdbcTemplate.update(sql.toString(), ps -> {
            int paramIndex = 1;
            for (Log log : logs) {
                ps.setLong(paramIndex++, log.getAppId());
                ps.setInt(paramIndex++, log.getLevel().ordinal());
                ps.setString(paramIndex++, log.getData().getValue());
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(log.getTimestamp()));
            }
        });
    }

    private final RowMapper<Log> LOG_ROW_MAPPER = (rs, rowNum) -> new Log(
        rs.getLong("log_id"),
        rs.getLong("app_id"),
        rs.getInt("level"),
        rs.getString("data"),
        rs.getTimestamp("timestamp").toLocalDateTime()
    );
}