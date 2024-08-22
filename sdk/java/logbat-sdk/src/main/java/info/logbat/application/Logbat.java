package info.logbat.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Logbat {

    private final AsyncLogWriter asyncLogWriter;
    private final LogSendRequestFactory logSendRequestFactory;

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
        if (isSupportedLevel(level)) {
            LogSendRequest logSendRequest = logSendRequestFactory.createLogSendRequest(eventObject);
            asyncLogWriter.sendLog(logSendRequest);
        }
    }

    private boolean isSupportedLevel(Level level) {
        return SUPPORTED_LEVELS.contains(level);
    }

    public Logbat(AsyncLogWriter asyncLogWriter, LogSendRequestFactory logSendRequestFactory) {
        this.asyncLogWriter = asyncLogWriter;
        this.logSendRequestFactory = logSendRequestFactory;
    }
}
