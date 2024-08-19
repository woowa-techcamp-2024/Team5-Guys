package info.logbat_view.domain.log.domain.enums;

import lombok.Getter;

@Getter
public enum LogLevel {
    TRACE(0), DEBUG(1), INFO(2), WARN(3), ERROR(4);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    public static LogLevel from(int value) {
        for (LogLevel level : LogLevel.values()) {
            if (level.getValue() == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("지정되지 않은 로그 레벨 오류입니다.");
    }
}
