package info.logbat.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import info.logbat.domain.log.Log;
import info.logbat.domain.log.enums.Level;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogBufferTest {

    private LogBuffer logBuffer;

    @BeforeEach
    void setUp() {
        logBuffer = new LogBuffer();
    }

    @DisplayName("로그를 추가할 수 있다.")
    @Test
    void addLog() {
        // given
        String level = "INFO";
        String data = "log data";
        LocalDateTime timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        Log log = new Log(level, data, timestamp);

        // when
        logBuffer.addLog(log);

        // then
        assertThat(logBuffer.getLogs(1))
            .hasSize(1)
            .extracting("level", "data.value", "timestamp")
            .containsExactly(
                tuple(Level.from(level), data, timestamp)
            );
    }

    @DisplayName("로그를 여러 개 가져올 수 있다.")
    @Test
    void getLogs() {
        // given
        String level = "INFO";

        for (int i = 0; i < 5; i++) {
            String data = "log data" + i;
            LocalDateTime timestamp = LocalDateTime.of(2021, 1, 1, 0, 0, i);
            Log log = new Log(level, data, timestamp);
            logBuffer.addLog(log);
        }

        // when
        final List<Log> logs = logBuffer.getLogs(5);

        // then
        assertThat(logs).hasSize(5)
            .extracting("level", "data.value", "timestamp")
            .containsExactly(
                tuple(Level.from(level), "log data0", LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
                tuple(Level.from(level), "log data1", LocalDateTime.of(2021, 1, 1, 0, 0, 1)),
                tuple(Level.from(level), "log data2", LocalDateTime.of(2021, 1, 1, 0, 0, 2)),
                tuple(Level.from(level), "log data3", LocalDateTime.of(2021, 1, 1, 0, 0, 3)),
                tuple(Level.from(level), "log data4", LocalDateTime.of(2021, 1, 1, 0, 0, 4))
            );
    }

}