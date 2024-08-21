package info.logbat.domain.project.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> getAppIdByToken(String token) {
        try {
            String sql = "SELECT id FROM apps WHERE app_key = UNHEX(REPLACE(?, '-', ''))";
            Long id = jdbcTemplate.queryForObject(sql, Long.class, token);
            return Optional.ofNullable(id);
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}
