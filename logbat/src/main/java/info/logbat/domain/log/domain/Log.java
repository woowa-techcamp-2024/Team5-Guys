package info.logbat.domain.log.domain;

import info.logbat.domain.log.domain.enums.Level;
import info.logbat.domain.log.domain.values.LogData;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Log {

  private final Long logId;
  private final Long applicationId;
  private final Level level;
  private final LogData data;
  private final LocalDateTime timestamp;

  public static Log of(Long applicationId, String level, String logData, LocalDateTime timestamp) {
    return new Log(applicationId, level, logData, timestamp);
  }

  public Log(Long applicationId, String level, String data, LocalDateTime timestamp) {
    this(null, applicationId, level, data, timestamp);
  }

  public Log(Long logId, Long applicationId, String level, String data,
      LocalDateTime timestamp) {
    this.logId = logId;
    validateApplicationId(applicationId);
    this.applicationId = applicationId;
    this.level = Level.from(level);
    this.data = LogData.from(data);
    validateTimestamp(timestamp);
    this.timestamp = timestamp;
  }

  private void validateApplicationId(Long applicationId) {
    if (applicationId == null) {
      throw new IllegalArgumentException("applicationId는 null이 될 수 없습니다.");
    }
  }

  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      throw new IllegalArgumentException("timestamp는 null이 될 수 없습니다.");
    }
  }
}