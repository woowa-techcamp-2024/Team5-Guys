package info.logbat.adapter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import info.logbat.LogbatFactory;
import info.logbat.application.Logbat;

public class LogbatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Logbat logbat;

    @Override
    public void start() {
        super.start();
        this.logbat = LogbatFactory.getInstance();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        logbat.writeLog(eventObject);
    }
}
