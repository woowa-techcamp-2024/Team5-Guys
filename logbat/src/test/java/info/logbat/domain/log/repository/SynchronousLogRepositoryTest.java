package info.logbat.domain.log.repository;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.domain.enums.Level;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SynchronousLogRepository는")
class SynchronousLogRepositoryTest {

    @Autowired
    private SynchronousLogRepository synchronousLogRepository;

    private final Long expectedLogId = 1L;
    private final String expectedLogLevel = "INFO";
    private final String expectedLogData = "테스트_로그_데이터";
    private final LocalDateTime expectedLogTimestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    private final Log expectedLog = new Log(expectedLogId, expectedLogLevel, expectedLogData,
        expectedLogTimestamp);

    @Test
    @DisplayName("Log를 저장할 수 있다.")
    void willSaveLog() {
        // Act
        long actualResult = synchronousLogRepository.save(expectedLog);
        // Assert
        assertThat(actualResult).isPositive();
    }

    @Nested
    @DisplayName("Log를 조회할 때")
    class whenFindLog {

        @Test
        @DisplayName("조회할 수 있다.")
        void willReturnLogWithOptionalWhenLogExists() {
            // Arrange
            long expectedSavedLogId = synchronousLogRepository.save(expectedLog);
            // Act
            Optional<Log> actualResult = synchronousLogRepository.findById(expectedSavedLogId);
            // Assert
            assertThat(actualResult).isPresent().get()
                .extracting("id", "appId", "level", "data.value", "timestamp")
                .containsExactly(expectedSavedLogId, expectedLog.getAppId(),
                    Level.valueOf(expectedLogLevel), expectedLog.getData().getValue(),
                    expectedLog.getTimestamp());
        }

        @Test
        @DisplayName("없는 Log를 조회하면 빈 Optional을 반환한다.")
        void findNonexistentLog() {
            // Arrange
            long nonexistentLogId = 1L;
            // Act
            Optional<Log> actualResult = synchronousLogRepository.findById(nonexistentLogId);
            // Assert
            assertThat(actualResult).isEmpty();
        }
    }

}
