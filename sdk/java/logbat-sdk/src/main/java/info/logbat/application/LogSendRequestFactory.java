package info.logbat.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return new LogSendRequest(
            getFormattedLevel(eventObject.getLevel()),
            getFormattedData(eventObject),
            formatTimestamp(eventObject.getTimeStamp())
        );
    }

    /**
     * 로그 메시지를 형식화하여 생성합니다. 형식: [ThreadName] [LoggerName] Level - Message (MDC 정보) (예외 정보)
     * <p>
     * MDC 정보와 예외 정보는 각각 존재하는 경우에만 메시지에 추가됩니다. 예시: [main] [com.example.MyClass] INFO - This is a
     * test message MDC: {userId=12345} EXCEPTION: NullPointerException
     */
    private String getFormattedData(ILoggingEvent eventObject) {
        return String.format(
            "[%s] [%s] %s - %s%s%s",
            eventObject.getThreadName(),
            Optional.ofNullable(eventObject.getLoggerName()).orElse("unknown"),
            getFormattedLevel(eventObject.getLevel()),
            eventObject.getFormattedMessage(),
            getMdcString(eventObject.getMDCPropertyMap()),
            getExceptionString(eventObject.getThrowableProxy())
        );
    }

    private String getFormattedLevel(Level level) {
        return SUPPORTED_LEVELS.contains(level) ? level.toString() : OTHER_LEVEL;
    }

    private String formatTimestamp(long timestamp) {
        if (timestamp <= 0) {
            return LocalDateTime.now().format(CUSTOM_FORMATTER);
        }
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            ZoneId.systemDefault()
        ).format(CUSTOM_FORMATTER);
    }

    private String getMdcString(Map<String, String> mdcProperties) {
        return mdcProperties.isEmpty() ? "" :
            " MDC: " + mdcProperties.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String getExceptionString(IThrowableProxy throwableProxy) {
        return Optional.ofNullable(throwableProxy)
            .map(t -> " EXCEPTION: " + t.getMessage())
            .orElse("");
    }
}
