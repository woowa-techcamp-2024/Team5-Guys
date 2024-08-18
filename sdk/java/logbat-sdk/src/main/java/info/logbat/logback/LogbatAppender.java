package info.logbat.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class LogbatAppender extends AppenderBase<ILoggingEvent> {

  @Override
  protected void append(ILoggingEvent eventObject) {
    String logMessage = eventObject.getFormattedMessage();
    System.out.println("Logbat Appender: " + logMessage);
  }
}
