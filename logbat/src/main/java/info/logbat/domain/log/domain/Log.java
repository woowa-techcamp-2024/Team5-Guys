package info.logbat.domain.log.domain;

import info.logbat.domain.log.domain.enums.Level;
import info.logbat.domain.log.domain.values.LogData;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Log {

  private final Long logId;
  private final String appKey;
  private final Level level;
  private final LogData data;
  private final LocalDateTime timestamp;

  public static Log of(String appKey, String level, String logData, LocalDateTime timestamp) {
    return new Log(appKey, level, logData, timestamp);
  }

  public Log(String appKey, String level, String data, LocalDateTime timestamp) {
    this(null, appKey, level, data, timestamp);
  }

  public Log(Long logId, String appKey, Integer level, String data,
      LocalDateTime timestamp) {
    this.logId = logId;
    validateAppKey(appKey);
    this.appKey = appKey;
    this.level = Level.from(level);
    this.data = LogData.from(data);
    validateTimestamp(timestamp);
    this.timestamp = timestamp;
  }

  public Log(Long logId, String appKey, String level, String data,
             LocalDateTime timestamp) {
    this.logId = logId;
    validateAppKey(appKey);
    this.appKey = appKey;
    this.level = Level.from(level);
    this.data = LogData.from(data);
    validateTimestamp(timestamp);
    this.timestamp = timestamp;
  }

  private void validateAppKey(String appKey) {
    if (appKey == null || appKey.isBlank()) {
      throw new IllegalArgumentException("appKey는 null이거나 빈 문자열이 될 수 없습니다.");
    }
  }

  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp == null) {
      throw new IllegalArgumentException("timestamp는 null이 될 수 없습니다.");
    }
  }
}