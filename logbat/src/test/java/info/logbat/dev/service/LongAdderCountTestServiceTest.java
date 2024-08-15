package info.logbat.dev.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("LongAdderCountTestServiceTest 테스트")
class LongAdderCountTestServiceTest {

  private LongAdderCountTestService longAdderCountTestService = new LongAdderCountTestService();

  @AfterEach
  void tearDown() {
    longAdderCountTestService.getSuccessCount();
    longAdderCountTestService.getErrorCount();
    longAdderCountTestService.reset();
  }

  @DisplayName("여러 스레드에서 카운트를 증가해도 동기화가 보장된다.")
  @Test
  @Disabled("도메인 로직과 관련 없는 테스트이므로 평소에는 비활성화")
  void testParallelIncrement() throws InterruptedException {
    long threadCount = 1000;
    long incrementsPerThread = 1000000;

    ExecutorService executorService = Executors.newFixedThreadPool((int) threadCount);

    List<Callable<Void>> tasks = new ArrayList<>((int) threadCount);
    for (int i = 0; i < threadCount; i++) {
      tasks.add(() -> {
        for (int j = 0; j < incrementsPerThread; j++) {
          longAdderCountTestService.increaseSuccessCount();
        }
        return null;
      });
    }

    executorService.invokeAll(tasks);
    executorService.shutdown();

    long successCount = longAdderCountTestService.getSuccessCount();

    assertThat(successCount).isEqualTo(threadCount * incrementsPerThread);
  }


  @Nested
  @DisplayName("성공 카운트 테스트")
  class SuccessCountTest {

    @Test
    @DisplayName("성공 카운트 증가")
    void testIncreaseSuccessCount() {
      // given
      long 예상_성공_카운트 = longAdderCountTestService.getSuccessCount() + 1;

      // when
      longAdderCountTestService.increaseSuccessCount();

      // then
      assertThat(longAdderCountTestService.getSuccessCount()).isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("성공 카운트 여러 번 증가")
    void testMultipleIncreaseSuccessCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_성공_카운트 = longAdderCountTestService.getSuccessCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        longAdderCountTestService.increaseSuccessCount();
      }

      // then
      assertThat(longAdderCountTestService.getSuccessCount())
          .isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("모든 스레드의 성공 카운트를 합산하여 반환한다")
    void returnsSumOfAllThreadsSuccessCount() throws InterruptedException {
      // given
      longAdderCountTestService.increaseSuccessCount();
      // 다른 스레드에서의 증가를 시뮬레이션
      Thread thread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
          longAdderCountTestService.increaseSuccessCount();
        }
      });
      thread.start();

      thread.join();

      // when
      long totalSuccessCount = longAdderCountTestService.getSuccessCount();

      // then
      assertThat(totalSuccessCount).isEqualTo(6);
    }

    @Test
    @DisplayName("정상 버퍼 크기에 도달하더라도 메인 카운터에 반영된다")
    void flushesToMainCounterWhenBufferIsFull() {
      // given
      long bufferSize = 1000L; // BUFFER_SIZE와 동일한 값

      // when
      for (int i = 0; i < bufferSize; i++) {
        longAdderCountTestService.increaseSuccessCount();
      }

      // then
      assertThat(longAdderCountTestService.getSuccessCount()).isEqualTo(bufferSize);
    }
  }

  @Nested
  @DisplayName("오류 카운트 테스트")
  class ErrorCountTest {

    @Test
    @DisplayName("오류 카운트 증가")
    void testIncreaseErrorCount() {
      // given
      long 예상_에러_카운트 = longAdderCountTestService.getErrorCount() + 1;

      // when
      longAdderCountTestService.increaseErrorCount();

      // then
      assertThat(longAdderCountTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("오류 카운트 여러 번 증가")
    void testMultipleIncreaseErrorCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_에러_카운트 = longAdderCountTestService.getErrorCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        longAdderCountTestService.increaseErrorCount();
      }

      // then
      assertThat(longAdderCountTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("모든 스레드의 에러 카운트를 합산하여 반환한다")
    void returnsSumOfAllThreadsErrorCount() throws InterruptedException {
      // given
      longAdderCountTestService.increaseErrorCount();

      Thread thread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
          longAdderCountTestService.increaseErrorCount();
        }
      });
      thread.start();

      thread.join();

      // when
      long totalErrorCount = longAdderCountTestService.getErrorCount();

      // then
      assertThat(totalErrorCount).isEqualTo(6);
    }

    @DisplayName("오류 버퍼 크기에 도달하더라도 메인 카운터에 반영된다")
    @Test
    void flushesToMainCounterWhenBufferIsFull() {
      // given
      long bufferSize = 1000L; // BUFFER_SIZE와 동일한 값

      // when
      for (int i = 0; i < bufferSize; i++) {
        longAdderCountTestService.increaseErrorCount();
      }

      // then
      assertThat(longAdderCountTestService.getErrorCount()).isEqualTo(bufferSize);
    }
  }

  @Test
  @DisplayName("카운트 초기화")
  void testReset() {
    // given
    longAdderCountTestService.increaseSuccessCount();
    longAdderCountTestService.increaseErrorCount();

    // when
    longAdderCountTestService.reset();

    // then
    assertAll(
        () -> assertThat(longAdderCountTestService.getSuccessCount()).isZero(),
        () -> assertThat(longAdderCountTestService.getErrorCount()).isZero()
    );
  }

}