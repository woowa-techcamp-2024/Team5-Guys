package info.logbat.domain.log.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import info.logbat.domain.log.domain.Log;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@DisplayName("AsyncLogRepository 테스트")
@SpringBootTest
class AsyncLogRepositoryTest {

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private AsyncLogProcessor asyncLogProcessor;

    @Autowired
    private AsyncLogRepository asyncLogRepository;

    @DisplayName("AsyncLogProcessor에 로그가 전달되는지 확인")
    @Test
    void testSave() {
        // given
        Log log = new Log(1L, UUID.randomUUID().toString(), 0, "Test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0));

        // When
        long result = asyncLogRepository.save(log);

        // Then
        assertThat(0L).isEqualTo(result);
        verify(asyncLogProcessor).submitLog(log);
    }

    @DisplayName("존재하는 로그 ID인 경우 Optional에 로그 반환")
    @Test
    void testFindById_WhenLogExists() {
        // given
        Long logId = 1L;
        Log expectedLog = new Log(logId, UUID.randomUUID().toString(), 0, "Test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(logId)))
            .thenReturn(expectedLog);

        // When
        Optional<Log> result = asyncLogRepository.findById(logId);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(expectedLog);
    }

    @DisplayName("존재하지 않는 로그 ID인 경우 빈 Optional 반환")
    @Test
    void testFindById_WhenLogDoesNotExist() {
        // given
        Long logId = 1L;
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(logId)))
            .thenThrow(new EmptyResultDataAccessException(1));

        // When
        Optional<Log> result = asyncLogRepository.findById(logId);

        // Then
        assertThat(result.isPresent()).isFalse();
    }
}