package info.logbat.infrastructure;

import info.logbat.infrastructure.payload.LogSendRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LogBuffer {

    private final LinkedBlockingQueue<LogSendRequest> logQueue;

    public void addLog(LogSendRequest logSendRequest) {
        logQueue.add(logSendRequest);
    }

    public List<LogSendRequest> getLogs(int count) {
        List<LogSendRequest> logs = new ArrayList<>();
        logQueue.drainTo(logs, count);
        return logs;
    }

    public boolean isEmpty() {
        return logQueue.isEmpty();
    }

    public LogBuffer() {
        this.logQueue = new LinkedBlockingQueue<>();
    }
}
