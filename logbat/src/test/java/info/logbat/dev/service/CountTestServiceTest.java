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
@DisplayName("CountTestService 테스트")
class CountTestServiceTest {

  @AfterEach
  void tearDown() {
    countTestService.reset();
  }

  @Autowired
  private CountTestService countTestService;

  @Nested
  @DisplayName("성공 카운트 테스트")
  class SuccessCountTest {

    @Test
    @DisplayName("성공 카운트 증가")
    void testIncreaseSuccessCount() {
      // given
      long 예상_성공_카운트 = countTestService.getSuccessCount() + 1;

      // when
      countTestService.increaseSuccessCount();

      // then
      assertThat(countTestService.getSuccessCount()).isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("성공 카운트 여러 번 증가")
    void testMultipleIncreaseSuccessCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_성공_카운트 = countTestService.getSuccessCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        countTestService.increaseSuccessCount();
      }

      // then
      assertThat(countTestService.getSuccessCount())
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
      long 예상_에러_카운트 = countTestService.getErrorCount() + 1;

      // when
      countTestService.increaseErrorCount();

      // then
      assertThat(countTestService.getErrorCount())
          .isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("오류 카운트 여러 번 증가")
    void testMultipleIncreaseErrorCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_에러_카운트 = countTestService.getErrorCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        countTestService.increaseErrorCount();
      }

      // then
      assertThat(countTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }
  }

  @Test
  @DisplayName("카운트 초기화")
  void testReset() {
    // given
    countTestService.increaseSuccessCount();
    countTestService.increaseErrorCount();

    // when
    countTestService.reset();

    // then
    assertAll(
        () -> assertThat(countTestService.getSuccessCount()).isZero(),
        () -> assertThat(countTestService.getErrorCount()).isZero()
    );
  }
}