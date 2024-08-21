package info.logbat;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.logbat.application.Logbat;
import info.logbat.domain.options.LogbatOptions;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.LogBuffer;
import info.logbat.infrastructure.LogSender;
import java.net.http.HttpClient;

public class LogbatFactory {

    private static volatile Logbat logbat;

    public synchronized static Logbat getInstance() {
        if (logbat == null) {
            synchronized (LogbatFactory.class) {
                if (logbat == null) {
                    logbat = createLogbat();
                }
            }
        }
        return logbat;
    }

    private static Logbat createLogbat() {
        LogbatOptions logbatOptions = new LogbatOptions();
        ObjectMapper objectMapper = new ObjectMapper();
        LogBuffer logBuffer = new LogBuffer();
        LogSender logSender =
            new LogSender(HttpClient.newHttpClient(), objectMapper, logbatOptions);
        AsyncLogWriter asyncLogWriter = new AsyncLogWriter(logBuffer, logSender);
        logbat = new Logbat(asyncLogWriter);
        return logbat;
    }

    /**
     * LogbatFactory는 인스턴스화할 수 없습니다.
     */
    private LogbatFactory() {
    }
}
