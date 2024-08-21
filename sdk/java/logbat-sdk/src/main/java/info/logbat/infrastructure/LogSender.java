package info.logbat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.logbat.domain.options.LogbatOptions;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.List;

public class LogSender {

    private static final String LOGBAT_HEADER = "App-Key";
    private static final Integer SUCCESS_STATUS_CODE = 201;

    private final LogbatOptions logbatOptions;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Builder requestBuilder;


    /**
     * sendLogs는 파라미터로 들어온 로그 데이터를 전송합니다. 파라미터로 들어온 로그 데이터가 비어있을 경우 아무 동작도 하지 않습니다.
     *
     * @param logSendRequests
     */
    public void sendLogs(List<LogSendRequest> logSendRequests) {
        if (logSendRequests.isEmpty()) {
            return;
        }

        String requestBody = prepareRequestBody(logSendRequests);

        HttpRequest request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == SUCCESS_STATUS_CODE) {
            return;
        }
        if (response.statusCode() == 400) {
            throw new IllegalArgumentException("Invalid log data");
        }
        throw new RuntimeException("Failed to send log data");
    }

    private String prepareRequestBody(List<LogSendRequest> logSendRequests) {
        try {
            return objectMapper.writeValueAsString(logSendRequests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize log data", e);
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send log data", e);
        }
    }

    public LogSender(HttpClient httpClient, ObjectMapper objectMapper,
        LogbatOptions logbatOptions) {
        this.logbatOptions = logbatOptions;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://api.logbat.info/logs"))
            .header("Content-Type", "application/json")
            .header(LOGBAT_HEADER, this.logbatOptions.getAppKey().getValue());
    }
}
