package info.logbat.infrastructure;

import info.logbat.infrastructure.payload.LogSendRequest;

public class AsyncLogWriter {

    private final LogBuffer logBuffer;
    private final LogSender logSender;
    private final LogProcessScheduler logProcessScheduler;

    /**
     * AsyncLogWriter는 LogBuffer와 LogSender를 생성자로 받아 LogProcessScheduler를 생성합니다.
     * <p>
     * LogProcessScheduler는 주기적으로 LogBuffer에 저장된 로그를 읽어와 LogSender를 통해 전송합니다.
     *
     * @param logBuffer
     * @param logSender
     */
    public AsyncLogWriter(LogBuffer logBuffer, LogSender logSender) {
        this.logSender = logSender;
        this.logBuffer = logBuffer;
        this.logProcessScheduler =
            new LogProcessScheduler(this.logSender::sendLogs, this.logBuffer);
    }

    /**
     * LogBuffer는 내부적으로 동기화된 LinkedBlockingQueue를 사용하여 로그를 저장합니다.
     *
     * @param logSendRequest
     */
    public void sendLog(LogSendRequest logSendRequest) {
        logBuffer.addLog(logSendRequest);
    }
}
