package info.logbat.domain.log;

import info.logbat.domain.log.enums.Level;
import info.logbat.domain.log.values.LogData;
import java.time.LocalDateTime;

public class Log {

    private final Level level;
    private final LogData data;
    private final LocalDateTime timestamp;

    public Level getLevel() {
        return level;
    }

    public LogData getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Log(String level, String data, LocalDateTime timestamp) {
        this.level = Level.from(level);
        this.data = LogData.from(data);
        this.timestamp = timestamp;
    }
}
