package info.logbat.adapter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import info.logbat.domain.options.LogbatOptions;
import info.logbat.application.Logbat;

public class LogbatAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private Logbat logbat;

    @Override
    public void start() {
        super.start();
        LogbatOptions logbatOptions = new LogbatOptions();
        logbat = new Logbat(logbatOptions);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
      String logMessage = eventObject.getFormattedMessage();
      System.out.println("Logbat Appender: " + logMessage);
    }
}
