package info.logbat;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.logbat.application.LogSendRequestFactory;
import info.logbat.application.Logbat;
import info.logbat.domain.options.LogbatOptions;
import info.logbat.exception.InvalidAppKeyException;
import info.logbat.infrastructure.AsyncLogWriter;
import info.logbat.infrastructure.LogBuffer;
import info.logbat.infrastructure.LogSender;
import java.net.http.HttpClient;

public final class LogbatFactory {

    /**
     * Logbat의 싱글톤 인스턴스입니다.
     */
    private static volatile Logbat logbat;

    /**
     * 싱글톤 패턴을 사용하여 Logbat 인스턴스를 반환합니다. 생성합니다.
     *
     * @return Logbat의 싱글톤 인스턴스
     */
    public static Logbat getInstance() {
        if (logbat == null) {
            synchronized (LogbatFactory.class) {
                if (logbat == null) {
                    try {
                        logbat = createLogbat();
                    } catch (InvalidAppKeyException e) {
                        System.err.println("Failed to create Logbat instance: " + e.getMessage());
                        System.exit(100);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return logbat;
    }

    /**
     * Logbat 인스턴스를 생성합니다. 이 메서드는 Logbat의 생성에 필요한 모든 종속 객체들을 초기화합니다.
     *
     * @return 초기화된 Logbat 인스턴스
     */
    private static Logbat createLogbat() throws InvalidAppKeyException {
        LogbatOptions logbatOptions = new LogbatOptions();
        ObjectMapper objectMapper = new ObjectMapper();
        LogBuffer logBuffer = new LogBuffer();
        LogSender logSender =
            new LogSender(HttpClient.newHttpClient(), objectMapper, logbatOptions);
        LogSendRequestFactory logSendRequestFactory = new LogSendRequestFactory();
        AsyncLogWriter asyncLogWriter = new AsyncLogWriter(logBuffer, logSender);
        logbat = new Logbat(asyncLogWriter, logSendRequestFactory);
        return logbat;
    }

    /**
     * LogbatFactory 클래스는 인스턴스화할 수 없도록 private 생성자를 가지고 있습니다.
     */
    private LogbatFactory() {
        /**
         * 리플렉션으로 호출될 수 없습니다.
         */
        throw new UnsupportedOperationException("LogbatFactory cannot be instantiated");
    }
}
