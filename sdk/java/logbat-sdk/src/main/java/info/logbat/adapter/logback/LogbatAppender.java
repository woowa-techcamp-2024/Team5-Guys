package info.logbat.adapter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import info.logbat.LogbatFactory;
import info.logbat.application.Logbat;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogbatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Logbat logbat;
    private static final DateTimeFormatter CUSTOM_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void start() {
        super.start();
        this.logbat = LogbatFactory.getInstance();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String level = eventObject.getLevel().toString();
        String message = eventObject.getFormattedMessage();
        String timestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(eventObject.getTimeStamp()),
            ZoneId.systemDefault()
        ).format(CUSTOM_FORMATTER);

        logbat.writeLog(new LogSendRequest(level, message, timestamp));
    }
}
