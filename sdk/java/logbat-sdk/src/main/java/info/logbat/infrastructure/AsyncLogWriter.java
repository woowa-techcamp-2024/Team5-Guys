package info.logbat.infrastructure;

import info.logbat.domain.log.Log;

public class AsyncLogWriter {

    private final LogBuffer logBuffer;
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
        this.logProcessScheduler = new LogProcessScheduler(logSender::sendLogs, logBuffer);
        this.logBuffer = logBuffer;
    }

    /**
     * LogBuffer는 내부적으로 동기화된 LinkedBlockingQueue를 사용하여 로그를 저장합니다.
     *
     * @param log
     */
    public void sendLog(Log log) {
        logBuffer.addLog(log);
    }
}
