package info.logbat.infrastructure;

import info.logbat.domain.log.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LogBuffer {

    private final LinkedBlockingQueue<Log> logQueue;

    public void addLog(Log log) {
        logQueue.add(log);
    }

    public List<Log> getLogs(int count) {
        List<Log> logs = new ArrayList<>();
        logQueue.drainTo(logs, count);
        return logs;
    }

    public LogBuffer(LinkedBlockingQueue<Log> logQueue) {
        this.logQueue = logQueue;
    }
}
