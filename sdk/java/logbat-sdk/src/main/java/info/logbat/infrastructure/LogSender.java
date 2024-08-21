package info.logbat.infrastructure;

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

    private static final String LOGBAT_HEADER = "appKey";
    private static final Integer SUCCESS_STATUS_CODE = 201;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Builder requestBuilder;
    
    public void sendLog(List<LogSendRequest> logSendRequests) {
        try {
            String requestBody = objectMapper.writeValueAsString(logSendRequests);

            HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != SUCCESS_STATUS_CODE) {
                System.err.println("Failed to send logs, response code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LogSender(ObjectMapper objectMapper, LogbatOptions logbatOptions) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("https://api.logbat.info/logs"))
            .header("Content-Type", "application/json")
            .header(LOGBAT_HEADER, logbatOptions.getAppKey().getValue());
    }
}
