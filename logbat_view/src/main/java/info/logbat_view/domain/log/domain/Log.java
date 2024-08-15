package info.logbat_view.domain.log.domain;

import info.logbat_view.domain.log.domain.enums.LogLevel;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Log {

    private final Long id;
    private final String appKey;
    private final LogLevel level;
    private final String data;
    private final LocalDateTime timestamp;

    public static Log from(LogData logData) {
        return new Log(logData.getLogId(),
            logData.getAppKey(),
            LogLevel.valueOf(logData.getLevel()),
            logData.getData(),
            logData.getTimestamp());
    }
}
