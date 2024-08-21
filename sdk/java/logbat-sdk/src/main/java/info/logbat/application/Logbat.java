package info.logbat.application;

import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.payload.LogSendRequest;

public class Logbat {

    private final AsyncLogWriter asyncLogWriter;

    public void writeLog(LogSendRequest log) {
        asyncLogWriter.sendLog(log);
    }

    public Logbat(AsyncLogWriter asyncLogWriter) {
        this.asyncLogWriter = asyncLogWriter;
    }
}
