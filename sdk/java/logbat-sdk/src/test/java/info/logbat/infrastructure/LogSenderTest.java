package info.logbat.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import info.logbat.domain.logbat.AppKey;
import info.logbat.domain.options.LogbatOptions;
import info.logbat.exception.InvalidAppKeyException;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogSenderTest {

    private LogSender logSender;
    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws InvalidAppKeyException {
        LogbatOptions logbatOptions = mock(LogbatOptions.class);
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        httpClient = mock(HttpClient.class);
        AppKey appKey = new AppKey(UUID.randomUUID().toString());
        when(logbatOptions.getAppKey()).thenReturn(appKey);

        logSender = new LogSender(httpClient, objectMapper, logbatOptions);
    }


    @DisplayName("로그를 전송할 수 있다.")
    @Test
    void sendLog() throws IOException, InterruptedException {
        // given
        LogSendRequest logSendRequest = new LogSendRequest("INFO", "test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0).toString());

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(201);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponse);

        // when
        logSender.sendLogs(List.of(logSendRequest));

        // then
        verify(httpClient)
            .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @DisplayName("로그를 잘못된 형식으로 전송하면 400이 발생한다.")
    @Test
    void sendLogFailed() throws IOException, InterruptedException {
        // given
        LogSendRequest logSendRequest = new LogSendRequest("INFO", "test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0).toString());

        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(httpResponse);

        // when & then
        assertThatThrownBy(() -> logSender.sendLogs(List.of(logSendRequest)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid log data");
        verify(httpClient)
            .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @DisplayName("로그 전송 중 에러가 발생하면 RuntimeException이 발생한다.")
    @Test
    void sendLogError() throws IOException, InterruptedException {
        // given
        LogSendRequest logSendRequest = new LogSendRequest("INFO", "test log data",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0).toString());

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException());

        // when & then
        assertThatThrownBy(() -> logSender.sendLogs(List.of(logSendRequest)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to send log data");
        verify(httpClient)
            .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}