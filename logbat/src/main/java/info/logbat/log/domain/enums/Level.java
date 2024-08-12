package info.logbat.log.domain.enums;

public enum Level {
  ERROR,
  INFO;

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
}
