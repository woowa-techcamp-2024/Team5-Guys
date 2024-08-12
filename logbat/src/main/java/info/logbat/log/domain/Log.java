package info.logbat.log.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Log {

  private final Long logId;
  private final Long applicationId;
  private final Level level;
  private final LogData logData;
  private final LocalDateTime timestamp;

  public Log(Long applicationId, String level, String logData, LocalDateTime timestamp) {
    this(null, applicationId, level, logData, timestamp);
  }

  public Log(Long logId, Long applicationId, String level, String logData,
      LocalDateTime timestamp) {
    this.logId = logId;
    validateApplicationId(applicationId);
    this.applicationId = applicationId;
    this.level = Level.from(level);
    this.logData = new LogData(logData);
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