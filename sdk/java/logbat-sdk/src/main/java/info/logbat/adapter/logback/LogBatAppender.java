package info.logbat.adapter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import info.logbat.LogBatFactory;
import info.logbat.application.LogBat;

public class LogBatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private LogBat logbat;

    @Override
    public void start() {
        super.start();
        this.logbat = LogBatFactory.getInstance();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        logbat.writeLog(eventObject);
    }
}
