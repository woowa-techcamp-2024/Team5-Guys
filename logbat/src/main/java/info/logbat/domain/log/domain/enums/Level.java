package info.logbat.domain.log.domain.enums;

public enum Level {
  TRACE, // 0
  DEBUG, // 1
  INFO, // 2
  WARN, // 3
  ERROR; // 4


  public static Level from(String level) {
    if (level == null || level.isBlank()) {
      throw new IllegalArgumentException("level은 null이거나 빈 문자열일 수 없습니다.");
    }

    String upperCaseLevel = level.trim().toUpperCase();

    for (Level logLevel : Level.values()) {
      if (logLevel.name().equals(upperCaseLevel)) {
        return logLevel;
      }
    }

    throw new IllegalArgumentException("level이 올바르지 않습니다.");
  }

  public static Level from(Integer level) {
    if (level == null) {
      throw new IllegalArgumentException("level은 null이 될 수 없습니다.");
    }

    for (Level logLevel : Level.values()) {
      if (logLevel.ordinal() == level) {
        return logLevel;
      }
    }

    throw new IllegalArgumentException("level이 올바르지 않습니다.");
  }
}
