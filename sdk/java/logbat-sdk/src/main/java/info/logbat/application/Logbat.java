package info.logbat.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class Logbat {

    private final AsyncLogWriter asyncLogWriter;

    private static final DateTimeFormatter CUSTOM_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // 로그 레벨 집합을 미리 정의
    private static final Set<Level> SUPPORTED_LEVELS = Set.of(
        Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR
    );

    public void writeLog(ILoggingEvent eventObject) {
        Level level = eventObject.getLevel();

        /**
         * 로그 레벨이 SUPPORTED_LEVELS에 포함되어 있는 경우에만 로그 전송
         */
        if (SUPPORTED_LEVELS.contains(level)) {
            String formattedLevel = level.toString();
            String timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(eventObject.getTimeStamp()),
                ZoneId.systemDefault()
            ).format(CUSTOM_FORMATTER);

            /**
             * thread 이름, logger 이름, MDC 정보를 문자열로 변환
             */
            String threadName = eventObject.getThreadName();
            String loggerName = eventObject.getLoggerName();
            Map<String, String> mdcProperties = eventObject.getMDCPropertyMap();
            String mdcString = mdcProperties.isEmpty() ? "" : " MDC: " + mdcProperties;

            /**
             * throwable 정보가 있는 경우 throwable 정보를 문자열로 변환
             */
            String throwableString = "";
            if (eventObject.getThrowableProxy() != null) {
                throwableString = " EXCEPTION: " + eventObject.getThrowableProxy().getMessage();
            }

            /**
             * thread 이름과 logger 이름을 포함한 로그 메시지 생성
             */
            String message = String.format(
                "[%s] [%s] %s - %s%s%s",
                threadName, loggerName, formattedLevel, eventObject.getFormattedMessage(),
                mdcString, throwableString
            );

            /**
             * LogSendRequest 객체 생성 및 로그 전송
             */
            asyncLogWriter.sendLog(new LogSendRequest(formattedLevel, message, timestamp));
        }
    }

    public Logbat(AsyncLogWriter asyncLogWriter) {
        this.asyncLogWriter = asyncLogWriter;
    }
}
