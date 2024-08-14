package info.logbat.domain.project.domain.log.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.domain.enums.Level;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Log 도메인 테스트")
class LogTest {

  @Nested
  @DisplayName("새로운 Log 도메인 객체 생성")
  class CreateNewLog {

    @Test
    @DisplayName("새로 만든 Log 도메인 객체를 만들 수 있다.")
    void createLog() {
      // given
      Long 어플리케이션_ID = 1L;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

      // when
      Log log = new Log(어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프);

      // then
      assertThat(log)
          .extracting("logId", "applicationId", "level", "data.value", "timestamp")
          .containsExactly(null, 1L, Level.INFO, 로그_데이터, 로그_타임스탬프);
    }

    @Test
    @DisplayName("applicationId가 null이면 예외가 발생한다.")
    void createLogWithNullApplicationId() {
      // given
      Long 어플리케이션_ID = null;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

      // when & then
      assertThatThrownBy(() -> new Log(어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("applicationId는 null이 될 수 없습니다.");
    }

    @Test
    @DisplayName("timestamp가 null이면 예외가 발생한다.")
    void createLogWithNullTimestamp() {
      // given
      Long 어플리케이션_ID = 1L;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = null;

      // when & then
      assertThatThrownBy(() -> new Log(어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("timestamp는 null이 될 수 없습니다.");
    }
  }

  @Nested
  @DisplayName("DB에 저장된 Log 도메인 객체 생성")
  class CreateExistingLog {

    @Test
    @DisplayName("DB에 저장된 Log 도메인 객체를 만들 수 있다.")
    void createLogWithLogId() {
      // given
      Long 로그_ID = 1L;
      Long 어플리케이션_ID = 1L;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

      // when
      Log log = new Log(로그_ID, 어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프);

      // then
      assertThat(log)
          .extracting("logId", "applicationId", "level", "data.value", "timestamp")
          .containsExactly(로그_ID, 1L, Level.INFO, 로그_데이터, 로그_타임스탬프);
    }

    @Test
    @DisplayName("applicationId가 null이면 예외가 발생한다.")
    void createLogWithNullApplicationIdWithLogId() {
      // given
      Long 로그_ID = 1L;
      Long 어플리케이션_ID = null;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

      // when & then
      assertThatThrownBy(() -> new Log(로그_ID, 어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("applicationId는 null이 될 수 없습니다.");
    }

    @Test
    @DisplayName("timestamp가 null이면 예외가 발생한다.")
    void createLogWithNullTimestampWithLogId() {
      // given
      Long 로그_ID = 1L;
      Long 어플리케이션_ID = 1L;
      String 로그_데이터 = "테스트_로그_데이터";
      String 로그_레벨 = "INFO";
      LocalDateTime 로그_타임스탬프 = null;

      // when & then
      assertThatThrownBy(() -> new Log(로그_ID, 어플리케이션_ID, 로그_레벨, 로그_데이터, 로그_타임스탬프))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("timestamp는 null이 될 수 없습니다.");
    }
  }
}