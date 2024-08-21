package info.logbat.infrastructure;

import info.logbat.domain.log.Log;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LogBuffer {

    private final LinkedBlockingQueue<LogSendRequest> logQueue;

    public void addLog(Log log) {
        logQueue.add(new LogSendRequest(log.getLevel().name(), log.getData().getValue(),
            log.getTimestamp()));
    }

    public List<LogSendRequest> getLogs(int count) {
        List<LogSendRequest> logs = new ArrayList<>();
        logQueue.drainTo(logs, count);
        return logs;
    }

    public LogBuffer() {
        this.logQueue = new LinkedBlockingQueue<>();
    }
}
