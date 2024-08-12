package info.logbat.log.domain;

public enum Level {
  ERROR,
  INFO;

  public static Level from(String level) {
    if (level == null || level.isBlank()) {
      throw new IllegalArgumentException("level은 null이거나 빈 문자열일 수 없습니다.");
    }
    return Level.valueOf(level.toUpperCase());
  }
}
