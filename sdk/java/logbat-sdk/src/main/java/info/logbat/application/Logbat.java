package info.logbat.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.payload.LogSendRequest;

public class Logbat {

    private final AsyncLogWriter asyncLogWriter;
    private final LogSendRequestFactory logSendRequestFactory;


    public void writeLog(ILoggingEvent eventObject) {
        LogSendRequest logSendRequest = logSendRequestFactory.createLogSendRequest(eventObject);
        asyncLogWriter.sendLog(logSendRequest);
    }


    public Logbat(AsyncLogWriter asyncLogWriter, LogSendRequestFactory logSendRequestFactory) {
        this.asyncLogWriter = asyncLogWriter;
        this.logSendRequestFactory = logSendRequestFactory;
    }
}
