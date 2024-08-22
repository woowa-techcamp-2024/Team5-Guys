package info.logbat.domain.log.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.logbat.domain.common.ControllerTestSupport;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("LogController는")
class LogControllerTest extends ControllerTestSupport {

    private static final String EXPECTED_APP_KEY_STRING = UUID.randomUUID().toString();

    private final String expectedLogLevel = "INFO";
    private final String expectedLogData = "테스트_로그_데이터";
    private final LocalDateTime expectedLogTimestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    private final List<CreateLogRequest> expectedRequests = List.of(
        new CreateLogRequest(expectedLogLevel, expectedLogData, expectedLogTimestamp),
        new CreateLogRequest(expectedLogLevel, expectedLogData, expectedLogTimestamp)
    );

    @Nested
    @DisplayName("POST 요청에 대해")
    class describePost {

        @Test
        @DisplayName("로그들를 정상적으로 생성한다.")
        void willSaveLogsSuccess() throws Exception {
            // Arrange
            MockHttpServletRequestBuilder post = post("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("App-Key", EXPECTED_APP_KEY_STRING)
                .content(objectMapper.writeValueAsString(expectedRequests));
            // Act & Assert
            mockMvc.perform(post)
                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("App-Key가 없으면 400 에러를 반환한다.")
        void willReturn400IfAppKeyIsMissing() throws Exception {
            // Arrange
            MockHttpServletRequestBuilder post = post("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedRequests));
            // Act & Assert
            mockMvc.perform(post)
                .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("잘못된 입력으로 로그 생성 시 400 에러를 반환한다.")
        @MethodSource("invalidLogCreationInputs")
        void willReturn400IfInvalidInput(String level, String data, LocalDateTime timestamp)
            throws Exception {
            // Arrange
            List<CreateLogRequest> expectedWrongRequest = List.of(
                new CreateLogRequest(level, data, timestamp));
            MockHttpServletRequestBuilder post = post("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("App-Key", EXPECTED_APP_KEY_STRING)
                .content(objectMapper.writeValueAsString(expectedWrongRequest));
            // Act & Assert
            mockMvc.perform(post)
                .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> invalidLogCreationInputs() {
            return Stream.of(
                Arguments.of(null, "테스트_로그_데이터", LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
                Arguments.of("  ", "테스트_로그_데이터", LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
                Arguments.of("INFO", null, LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
                Arguments.of("INFO", "  ", LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
                Arguments.of("INFO", "테스트_로그_데이터", null)
            );
        }

    }

}