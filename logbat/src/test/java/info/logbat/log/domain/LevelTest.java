package info.logbat.log.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LevelTest {

  @DisplayName("빈 문자열의 로그 레벨 문자열을 enum으로 변환하면 예외가 발생한다.")
  @Test
  void createLevelWithBlank() {
    // given
    String 빈_로그_레벨 = "";

    // when & then
    assertThatThrownBy(() -> Level.from(빈_로그_레벨))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("level은 null이거나 빈 문자열일 수 없습니다.");
  }

  @DisplayName("null의 로그 레벨 문자열을 enum으로 변환하면 예외가 발생한다.")
  @Test
  void createLevelWithNull() {
    // given
    String null_로그_레벨 = null;

    // when & then
    assertThatThrownBy(() -> Level.from(null_로그_레벨))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("level은 null이거나 빈 문자열일 수 없습니다.");
  }

  @DisplayName("다양한 형식의 로그 레벨 문자열을 enum으로 변환할 수 있다.")
  @ParameterizedTest(name = "{index}. 입력값: {0}, 기대값: {1} enum")
  @MethodSource("logLevelProvider")
  void createLevel(String inputLevel, Level expectedLevel) {
    // when
    Level 로그_레벨 = Level.from(inputLevel);

    // then
    assertThat(로그_레벨).isEqualTo(expectedLevel);
  }

  private static Stream<Arguments> logLevelProvider() {
    return Stream.of(
        Arguments.of("info", Level.INFO),
        Arguments.of("iNfO", Level.INFO),
        Arguments.of("Info", Level.INFO),
        Arguments.of("INFO", Level.INFO),
        Arguments.of("error", Level.ERROR),
        Arguments.of("ErRoR", Level.ERROR),
        Arguments.of("ERROR", Level.ERROR)
    );
  }
}