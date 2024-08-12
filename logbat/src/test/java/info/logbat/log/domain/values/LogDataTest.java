package info.logbat.log.domain.values;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogData VO 테스트")
class LogDataTest {

  @DisplayName("정상적으로 LogData를 만들 수 있다.")
  @Test
  void createLogData() {
    // given
    String 로그_데이터 = "테스트_로그_데이터";

    // when
    LogData logData = new LogData(로그_데이터);

    // then
    assertThat(logData)
        .extracting("value")
        .isEqualTo(로그_데이터);
  }

  @DisplayName("빈 문자열로 LogData를 만들면 예외가 발생한다.")
  @Test
  void createLogDataWithBlank() {
    // given
    String 빈_로그_데이터 = "";

    // when & then
    assertThatThrownBy(() -> new LogData(빈_로그_데이터))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("log data는 null이거나 빈 문자열일 수 없습니다.");
  }

  @DisplayName("null로 LogData를 만들면 예외가 발생한다.")
  @Test
  void createLogDataWithNull() {
    // given
    String null_로그_데이터 = null;

    // when & then
    assertThatThrownBy(() -> new LogData(null_로그_데이터))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("log data는 null이거나 빈 문자열일 수 없습니다.");
  }
}