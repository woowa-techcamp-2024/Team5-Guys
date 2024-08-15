package info.logbat.dev.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SimpleCountTestService 테스트")
class SimpleCountTestServiceTest {

  @AfterEach
  void tearDown() {
    simpleCountTestService.reset();
  }

  @Autowired
  private SimpleCountTestService simpleCountTestService;

  @Nested
  @DisplayName("성공 카운트 테스트")
  class SuccessCountTest {

    @Test
    @DisplayName("성공 카운트 증가")
    void testIncreaseSuccessCount() {
      // given
      long 예상_성공_카운트 = simpleCountTestService.getSuccessCount() + 1;

      // when
      simpleCountTestService.increaseSuccessCount();

      // then
      assertThat(simpleCountTestService.getSuccessCount()).isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("성공 카운트 여러 번 증가")
    void testMultipleIncreaseSuccessCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_성공_카운트 = simpleCountTestService.getSuccessCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        simpleCountTestService.increaseSuccessCount();
      }

      // then
      assertThat(simpleCountTestService.getSuccessCount())
          .isEqualTo(예상_성공_카운트);
    }
  }

  @Nested
  @DisplayName("오류 카운트 테스트")
  class ErrorCountTest {

    @Test
    @DisplayName("오류 카운트 증가")
    void testIncreaseErrorCount() {
      // given
      long 예상_에러_카운트 = simpleCountTestService.getErrorCount() + 1;

      // when
      simpleCountTestService.increaseErrorCount();

      // then
      assertThat(simpleCountTestService.getErrorCount())
          .isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("오류 카운트 여러 번 증가")
    void testMultipleIncreaseErrorCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_에러_카운트 = simpleCountTestService.getErrorCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        simpleCountTestService.increaseErrorCount();
      }

      // then
      assertThat(simpleCountTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }
  }

  @Test
  @DisplayName("카운트 초기화")
  void testReset() {
    // given
    simpleCountTestService.increaseSuccessCount();
    simpleCountTestService.increaseErrorCount();

    // when
    simpleCountTestService.reset();

    // then
    assertAll(
        () -> assertThat(simpleCountTestService.getSuccessCount()).isZero(),
        () -> assertThat(simpleCountTestService.getErrorCount()).isZero()
    );
  }
}