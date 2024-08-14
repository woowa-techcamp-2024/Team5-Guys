package info.logbat.domain.log.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.logbat.domain.common.ControllerTestSupport;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class LogControllerTest extends ControllerTestSupport {

  private static final String 앱_키_문자열 = UUID.randomUUID().toString();

  @DisplayName("[POST] 로그를 정상적으로 생성한다.")
  @Test
  void createLog() throws Exception {
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
        .header("appKey", 앱_키_문자열)
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
  void createLogWithInvalidInput(String testCase, String appKey, String level, String data,
      LocalDateTime timestamp) throws Exception {
    // given
    CreateLogRequest 요청_DTO = new CreateLogRequest(level, data, timestamp);
    when(logService.saveLog(any())).thenReturn(1L);

    // when
    ResultActions perform = mockMvc.perform(post("/logs")
        .contentType(MediaType.APPLICATION_JSON)
        .header("appKey", 앱_키_문자열)
        .content(objectMapper.writeValueAsString(요청_DTO)));

    // then
    perform.andExpect(status().isBadRequest());
  }

  private static Stream<Arguments> invalidLogCreationInputs() {
    return Stream.of(
        Arguments.of("로그_레벨이 null인 경우", 앱_키_문자열, null, "테스트_로그_데이터",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_레벨이 빈 문자열인 경우", 앱_키_문자열, "  ", "테스트_로그_데이터",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_데이터가 null인 경우", 앱_키_문자열, "INFO", null,
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("로그_데이터가 빈 문자열인 경우", 앱_키_문자열, "INFO", "  ",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0)),
        Arguments.of("타임스탬프가 null인 경우", 앱_키_문자열, "INFO", "테스트_로그_데이터", null)
    );
  }
}