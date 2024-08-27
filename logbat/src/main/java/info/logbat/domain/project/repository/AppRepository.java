package info.logbat.domain.project.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AppRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> getAppIdByToken(String token) {
        String selectQuery = "SELECT id FROM apps WHERE app_key = UNHEX(REPLACE(?, '-', ''))";
        try {
            Long id = jdbcTemplate.queryForObject(selectQuery, Long.class, token);
            return Optional.ofNullable(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
