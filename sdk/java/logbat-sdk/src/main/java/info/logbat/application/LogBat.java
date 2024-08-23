package info.logbat.application;

import ch.qos.logback.classic.spi.ILoggingEvent;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.payload.LogSendRequest;

/**
 * The LogBat class is responsible for writing log events asynchronously. It uses an AsyncLogWriter
 * to send log requests and a LogSendRequestFactory to create log send requests from logging
 * events.
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>
 * @version 0.1.0
 * @see AsyncLogWriter
 * @see LogSendRequestFactory
 * @since 0.1.0
 */
public class LogBat {

    private final AsyncLogWriter asyncLogWriter;
    private final LogSendRequestFactory logSendRequestFactory;

    /**
     * Constructs a new LogBat instance.
     *
     * @param asyncLogWriter        The AsyncLogWriter used to send log requests asynchronously.
     * @param logSendRequestFactory The factory used to create LogSendRequest objects from logging
     *                              events.
     */
    public LogBat(AsyncLogWriter asyncLogWriter, LogSendRequestFactory logSendRequestFactory) {
        this.asyncLogWriter = asyncLogWriter;
        this.logSendRequestFactory = logSendRequestFactory;
    }

    /**
     * Writes a log event asynchronously. This method creates a LogSendRequest from the given
     * logging event and sends it using the AsyncLogWriter.
     *
     * @param eventObject The ILoggingEvent to be written as a log.
     */
    public void writeLog(ILoggingEvent eventObject) {
        LogSendRequest logSendRequest = logSendRequestFactory.createLogSendRequest(eventObject);
        asyncLogWriter.sendLog(logSendRequest);
    }

}