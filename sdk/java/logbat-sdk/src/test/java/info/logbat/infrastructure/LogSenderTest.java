package info.logbat.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import info.logbat.domain.logbat.AppKey;
import info.logbat.domain.options.LogBatOptions;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LogSender는")
@SuppressWarnings("unchecked")
class LogSenderTest {


    private final HttpClient httpClient = mock(HttpClient.class);
    private final List<LogSendRequest> expectedLogSendRequests = List.of(
        new LogSendRequest("INFO", "test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0).toString()));
    private final AppKey expectedAppKey = mock(AppKey.class);

    private LogSender logSender;


    @BeforeEach
    void init() throws Exception {
        LogBatOptions logBatOptions = mock(LogBatOptions.class);
        given(logBatOptions.getAppKey()).willReturn(expectedAppKey);
        given(expectedAppKey.getValue()).willReturn("test-app-key");
        logSender = new LogSender(logBatOptions);
        Field field = logSender.getClass().getDeclaredField("httpClient");
        field.setAccessible(true);
        field.set(logSender, httpClient);
    }

    @Nested
    @DisplayName("로그를 전송할 때")
    class whenSendLog {

        private final HttpResponse<String> httpResponse = mock(HttpResponse.class);

        @Test
        @DisplayName("정상적으로 전송할 수 있다.")
        void willSuccess() throws IOException, InterruptedException {
            // Arrange
            given(httpResponse.statusCode()).willReturn(201);
            given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .willReturn(httpResponse);
            // Act
            logSender.sendLogs(expectedLogSendRequests);
            // Assert
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("잘못된 형식으로 전송하면 예외가 발생한다.")
        void willThrowExceptionWhenSendInvalidLog() throws IOException, InterruptedException {
            // Arrange
            given(httpResponse.statusCode()).willReturn(400);
            given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .willReturn(httpResponse);
            // Act & Assert
            assertThatThrownBy(() -> logSender.sendLogs(expectedLogSendRequests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid log data");
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("전송 중 에러가 발생하면 예외가 발생한다.")
        void willThrowExceptionWhenSendError() throws IOException, InterruptedException {
            // Arrange
            given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .willThrow(new IOException());
            // Act & Assert
            assertThatThrownBy(() -> logSender.sendLogs(expectedLogSendRequests))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to send log data");
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }
    }

}
