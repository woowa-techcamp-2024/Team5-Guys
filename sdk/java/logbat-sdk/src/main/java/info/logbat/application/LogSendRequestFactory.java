package info.logbat.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class LogSendRequestFactory {

    private static final DateTimeFormatter CUSTOM_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * Logbat에서 기본적으로 지원하는 로그 레벨
     */
    private static final Set<Level> SUPPORTED_LEVELS = Set.of(
        Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR
    );
    private static final String OTHER_LEVEL = "DEBUG";

    /**
     * ILoggingEvent로부터 LogSendRequest를 생성하는 메서드
     */
    public LogSendRequest createLogSendRequest(ILoggingEvent eventObject) {

        Level level = eventObject.getLevel();
        String formattedLevel = getFormattedLevel(level);
        String timestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(eventObject.getTimeStamp()),
            ZoneId.systemDefault()
        ).format(CUSTOM_FORMATTER);

        String threadName = eventObject.getThreadName();
        String loggerName = eventObject.getLoggerName();
        Map<String, String> mdcProperties = eventObject.getMDCPropertyMap();
        String mdcString = mdcProperties.isEmpty() ? "" : " MDC: " + mdcProperties;

        String throwableString = "";
        if (eventObject.getThrowableProxy() != null) {
            throwableString = " EXCEPTION: " + eventObject.getThrowableProxy().getMessage();
        }

        String message = String.format(
            "[%s] [%s] %s - %s%s%s",
            threadName, loggerName, formattedLevel, eventObject.getFormattedMessage(),
            mdcString, throwableString
        );

        return new LogSendRequest(formattedLevel, message, timestamp);
    }

    private String getFormattedLevel(Level level) {
        return isSupportedLevel(level) ? level.toString() : OTHER_LEVEL;
    }

    private boolean isSupportedLevel(Level level) {
        return SUPPORTED_LEVELS.contains(level);
    }

}
