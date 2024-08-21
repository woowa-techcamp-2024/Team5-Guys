package info.logbat.application;

import info.logbat.domain.log.Log;
import info.logbat.infrastructure.AsyncLogWriter;

public class Logbat {

    private final AsyncLogWriter asyncLogWriter;

    public void writeLog(Log log) {
        asyncLogWriter.sendLog(log);
    }

    public Logbat(AsyncLogWriter asyncLogWriter) {
        this.asyncLogWriter = asyncLogWriter;
    }
}
