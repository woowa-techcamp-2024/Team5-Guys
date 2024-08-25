package info.logbat.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LogBuffer는")
class LogBufferTest {

    private final String expectedLevel = "INFO";
    private final String expectedData = "log data";
    private final LocalDateTime expectedTimestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    private LogBuffer logBuffer;

    @BeforeEach
    void setUp() {
        logBuffer = new LogBuffer();
    }

    @Nested
    @DisplayName("로그를 추가할 때")
    class whenAddLog {

        @Test
        @DisplayName("정상적으로 추가한다.")
        void willAddSuccess() {
            // Arrange
            LogSendRequest logSendRequest = new LogSendRequest(expectedLevel, expectedData,
                expectedTimestamp.toString());
            // Act
            logBuffer.addLog(logSendRequest);
            // Assert
            assertThat(logBuffer.getLogs(1))
                .hasSize(1)
                .extracting("level", "data", "timestamp")
                .containsExactly(
                    tuple(expectedLevel, expectedData, expectedTimestamp.toString())
                );
        }
    }

    @Nested
    @DisplayName("로그를 조회할 때")
    class whenGetLog {

        @Test
        @DisplayName("로그가 없으면 빈 리스트를 반환한다.")
        void willReturnEmptyList() {
            // Act
            List<LogSendRequest> actualResult = logBuffer.getLogs(1);
            // Assert
            assertThat(actualResult).isEmpty();
        }

        @Test
        @DisplayName("여러 개의 로그를 반환한다.")
        void willReturnNLogs() {
            // Arrange
            for (int i = 0; i < 5; i++) {
                String data = expectedData + i;
                LocalDateTime timestamp = expectedTimestamp.plusSeconds(i);
                LogSendRequest logSendRequest = new LogSendRequest(expectedLevel, data,
                    timestamp.toString());
                logBuffer.addLog(logSendRequest);
            }
            // Act
            List<LogSendRequest> actualResult = logBuffer.getLogs(5);

            // then
            assertThat(actualResult).hasSize(5)
                .extracting("level", "data", "timestamp")
                .containsExactly(
                    tuple(expectedLevel, "log data0", expectedTimestamp.plusSeconds(0).toString()),
                    tuple(expectedLevel, "log data1", expectedTimestamp.plusSeconds(1).toString()),
                    tuple(expectedLevel, "log data2", expectedTimestamp.plusSeconds(2).toString()),
                    tuple(expectedLevel, "log data3", expectedTimestamp.plusSeconds(3).toString()),
                    tuple(expectedLevel, "log data4", expectedTimestamp.plusSeconds(4).toString())
                );
        }

    }

}