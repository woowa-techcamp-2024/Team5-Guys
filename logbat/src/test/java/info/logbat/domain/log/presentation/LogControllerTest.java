package info.logbat.domain.log.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.logbat.domain.common.ControllerTestSupport;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class LogControllerTest extends ControllerTestSupport {

  @DisplayName("[POST] 로그를 정상적으로 생성한다.")
  @Test
  void createLog() throws Exception {
    // given
    Long 어플리케이션_ID = 1L;
    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogRequest 요청_DTO = new CreateLogRequest(로그_레벨, 로그_데이터, 타임스탬프);

    when(logService.saveLog(any()))
        .thenReturn(1L);

    // when
    ResultActions perform = mockMvc.perform(post("/logs")
        .contentType(MediaType.APPLICATION_JSON)
        .header("app-id", 어플리케이션_ID)
        .content(objectMapper.writeValueAsString(요청_DTO)));

    // then
    perform.andExpect(status().isOk());
  }

  @DisplayName("[POST] 어플리케이션_ID가 없으면 400 에러를 반환한다.")
  @Test
  void createLogWithoutAppId() throws Exception {
    // given
    String 로그_레벨 = "INFO";
    String 로그_데이터 = "테스트_로그_데이터";
    LocalDateTime 타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    CreateLogRequest 요청_DTO = new CreateLogRequest(로그_레벨, 로그_데이터, 타임스탬프);

    when(logService.saveLog(any()))
        .thenReturn(1L);

    // when
    ResultActions perform = mockMvc.perform(post("/logs")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(요청_DTO)));

    // then
    perform.andExpect(status().isBadRequest());
  }

  @DisplayName("[POST] 잘못된 입력으로 로그 생성 시 400 에러를 반환한다.")
  @ParameterizedTest(name = "{index}: {0}")
  @MethodSource("invalidLogCreationInputs")
  void createLogWithInvalidInput(String testCase, Long appId, String level, String data,
      LocalDateTime timestamp) throws Exception {
    // given
    CreateLogRequest 요청_DTO = new CreateLogRequest(level, data, timestamp);
    when(logService.saveLog(any())).thenReturn(1L);

    // when
    ResultActions perform = mockMvc.perform(post("/logs")
        .contentType(MediaType.APPLICATION_JSON)
        .header("app-id", appId)
        .content(objectMapper.writeValueAsString(요청_DTO)));

    // then
    perform.andExpect(status().isBadRequest());
  }

  private static Stream<Arguments> invalidLogCreationInputs() {
    return Stream.of(
        Arguments.of("어플리케이션_ID가 음수인 경우", -1L, "INFO", "테스트_로그_데이터",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_레벨이 null인 경우", 1L, null, "테스트_로그_데이터",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_레벨이 빈 문자열인 경우", 1L, "  ", "테스트_로그_데이터",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_데이터가 null인 경우", 1L, "INFO", null,
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_데이터가 빈 문자열인 경우", 1L, "INFO", "  ",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("타임스탬프가 null인 경우", 1L, "INFO", "테스트_로그_데이터", null)
    );
  }
}