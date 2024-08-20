package info.logbat.domain.log.domain;

import info.logbat.domain.log.domain.enums.Level;
import info.logbat.domain.log.domain.values.LogData;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Log {

    private final Long id;
    private final Long appId;
    private final Level level;
    private final LogData data;
    private final LocalDateTime timestamp;

    public static Log of(Long appId, String level, String logData, LocalDateTime timestamp) {
        return new Log(appId, level, logData, timestamp);
    }

    public Log(Long appId, String level, String data, LocalDateTime timestamp) {
        this(null, appId, level, data, timestamp);
    }

    public Log(Long id, Long appId, Integer level, String data, LocalDateTime timestamp) {
        this.id = id;
        validateAppId(appId);
        this.appId = appId;
        this.level = Level.from(level);
        this.data = LogData.from(data);
        validateTimestamp(timestamp);
        this.timestamp = timestamp;
    }

    public Log(Long id, Long appId, String level, String data, LocalDateTime timestamp) {
        this.id = id;
        validateAppId(appId);
        this.appId = appId;
        this.level = Level.from(level);
        this.data = LogData.from(data);
        validateTimestamp(timestamp);
        this.timestamp = timestamp;
    }

    private void validateAppId(Long appId) {
        if (appId == null || appId <= 0) {
            throw new IllegalArgumentException("appId는 null일 수 없고 0보다 커야 합니다.");
        }
    }

    private void validateTimestamp(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp는 null이 될 수 없습니다.");
        }
    }
}