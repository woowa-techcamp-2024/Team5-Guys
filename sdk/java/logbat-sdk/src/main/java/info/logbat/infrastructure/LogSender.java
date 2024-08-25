package info.logbat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.logbat.domain.options.LogBatOptions;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * LogSender is responsible for sending log data to the LogBat API. It handles the HTTP
 * communication, serialization of log data, and error handling.
 *
 * @author KyungMin Lee <a href="https://github.com/tidavid1">GitHub</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class LogSender {

    private static final String LOG_BAT_HEADER = "App-Key";
    private static final String LOG_BAT_API_URL = "https://api.logbat.info/logs";
    private static final Integer CREATED_STATUS_CODE = 201;
    private static final Integer BAD_REQUEST_STATUS_CODE = 400;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Builder requestBuilder;

    /**
     * Constructs a new LogSender with the specified LogBatOptions.
     *
     * @param logBatOptions The options containing the LogBat API key and other configurations.
     */
    public LogSender(LogBatOptions logBatOptions) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(LOG_BAT_API_URL))
            .header("Content-Type", "application/json")
            .header(LOG_BAT_HEADER, logBatOptions.getAppKey().getValue());
    }

    /**
     * Sends the provided log data to the LogBat API. If the list of log requests is empty, this
     * method does nothing.
     *
     * @param logSendRequests A list of LogSendRequest objects containing the log data to be sent.
     * @throws IllegalArgumentException if the log data is invalid (results in a 400 Bad Request
     *                                  response).
     * @throws RuntimeException         if there's a failure in sending the log data or processing
     *                                  the response.
     */
    public void sendLogs(List<LogSendRequest> logSendRequests) {
        if (logSendRequests.isEmpty()) {
            return;
        }

        String requestBody = serializeLogRequests(logSendRequests);
        HttpRequest request = buildHttpRequest(requestBody);
        HttpResponse<String> response = executeRequest(request);

        handleResponse(response);
    }

    /**
     * Serializes the list of LogSendRequest objects into a JSON string.
     *
     * @param logSendRequests The list of LogSendRequest objects to serialize.
     * @return A JSON string representation of the log requests.
     * @throws RuntimeException if serialization fails.
     */
    private String serializeLogRequests(List<LogSendRequest> logSendRequests) {
        try {
            return objectMapper.writeValueAsString(logSendRequests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize log data", e);
        }
    }

    /**
     * Builds an HTTP request with the given request body.
     *
     * @param requestBody The serialized log data to be sent in the request body.
     * @return An HttpRequest object ready to be sent.
     */
    private HttpRequest buildHttpRequest(String requestBody) {
        return requestBuilder
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
    }

    /**
     * Executes the HTTP request and returns the response.
     *
     * @param request The HttpRequest to be sent.
     * @return The HttpResponse received from the server.
     * @throws RuntimeException if there's a failure in sending the request or receiving the
     *                          response.
     */
    private HttpResponse<String> executeRequest(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send log data", e);
        }
    }

    /**
     * Handles the HTTP response, throwing appropriate exceptions for non-successful responses.
     *
     * @param response The HttpResponse to be handled.
     * @throws IllegalArgumentException if the response status code is 400 (Bad Request).
     * @throws RuntimeException         if the response status code is neither 201 (Created) nor 400
     *                                  (Bad Request).
     */
    private void handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode == CREATED_STATUS_CODE) {
            return;
        }
        if (statusCode == BAD_REQUEST_STATUS_CODE) {
            throw new IllegalArgumentException("Invalid log data");
        } else {
            throw new RuntimeException("Failed to send log data: " + response.body());
        }
    }

}
