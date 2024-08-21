package info.logbat.adapter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import info.logbat.LogbatFactory;
import info.logbat.application.Logbat;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LogbatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Logbat logbat;

    @Override
    public void start() {
        super.start();
        this.logbat = LogbatFactory.getInstance();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String level = eventObject.getLevel().toString();
        String message = eventObject.getFormattedMessage();
        LocalDateTime timestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(eventObject.getTimeStamp()),
            ZoneId.systemDefault()
        );
        logbat.writeLog(new LogSendRequest(level, message, timestamp));
    }
}
