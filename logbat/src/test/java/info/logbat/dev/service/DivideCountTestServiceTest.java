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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CountTestService 테스트")
class DivideCountTestServiceTest {

  @Autowired
  private DivideCountTestService divideCountTestService;

  @AfterEach
  void tearDown() {
    divideCountTestService.getSuccessCount();
    divideCountTestService.getErrorCount();
    divideCountTestService.reset();
  }

  @DisplayName("여러 스레드에서 카운트를 증가해도 동기화가 보장된다.")
  @Test
  @Disabled("도메인 로직과 관련 없는 테스트이므로 평소에는 비활성화")
  void testParallelIncrement() throws InterruptedException {
    long threadCount = 100;
    long incrementsPerThread = 1000000;

    ExecutorService executorService = Executors.newFixedThreadPool((int) threadCount);

    List<Callable<Void>> tasks = new ArrayList<>((int) threadCount);
    for (int i = 0; i < threadCount; i++) {
      tasks.add(() -> {
        for (int j = 0; j < incrementsPerThread; j++) {
          divideCountTestService.increaseSuccessCount();
          divideCountTestService.increaseErrorCount();
        }
        return null;
      });
    }

    executorService.invokeAll(tasks);
    executorService.shutdown();

    long successCount = divideCountTestService.getSuccessCount();
    long errorCount = divideCountTestService.getErrorCount();

    assertAll(
        () -> assertThat(successCount).isEqualTo(threadCount * incrementsPerThread),
        () -> assertThat(errorCount).isEqualTo(threadCount * incrementsPerThread)
    );
    divideCountTestService.reset();
  }


  @Nested
  @DisplayName("성공 카운트 테스트")
  class SuccessCountTest {

    @Test
    @DisplayName("성공 카운트 증가")
    void testIncreaseSuccessCount() {
      // given
      long 예상_성공_카운트 = divideCountTestService.getSuccessCount() + 1;

      // when
      divideCountTestService.increaseSuccessCount();

      // then
      assertThat(divideCountTestService.getSuccessCount()).isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("성공 카운트 여러 번 증가")
    void testMultipleIncreaseSuccessCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_성공_카운트 = divideCountTestService.getSuccessCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        divideCountTestService.increaseSuccessCount();
      }

      // then
      assertThat(divideCountTestService.getSuccessCount())
          .isEqualTo(예상_성공_카운트);
    }

    @Test
    @DisplayName("모든 스레드의 성공 카운트를 합산하여 반환한다")
    void returnsSumOfAllThreadsSuccessCount() throws InterruptedException {
      // given
      divideCountTestService.increaseSuccessCount();
      // 다른 스레드에서의 증가를 시뮬레이션
      Thread thread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
          divideCountTestService.increaseSuccessCount();
        }
      });
      thread.start();

      thread.join();

      // when
      long totalSuccessCount = divideCountTestService.getSuccessCount();

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
        divideCountTestService.increaseSuccessCount();
      }

      // then
      assertThat(divideCountTestService.getSuccessCount()).isEqualTo(bufferSize);
    }
  }

  @Nested
  @DisplayName("오류 카운트 테스트")
  class ErrorCountTest {

    @Test
    @DisplayName("오류 카운트 증가")
    void testIncreaseErrorCount() {
      // given
      long 예상_에러_카운트 = divideCountTestService.getErrorCount() + 1;

      // when
      divideCountTestService.increaseErrorCount();

      // then
      assertThat(divideCountTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("오류 카운트 여러 번 증가")
    void testMultipleIncreaseErrorCount() {
      // given
      int 증가_횟수 = 5;
      long 예상_에러_카운트 = divideCountTestService.getErrorCount() + 증가_횟수;

      // when
      for (int i = 0; i < 증가_횟수; i++) {
        divideCountTestService.increaseErrorCount();
      }

      // then
      assertThat(divideCountTestService.getErrorCount()).isEqualTo(예상_에러_카운트);
    }

    @Test
    @DisplayName("모든 스레드의 에러 카운트를 합산하여 반환한다")
    void returnsSumOfAllThreadsErrorCount() throws InterruptedException {
      // given
      divideCountTestService.increaseErrorCount();

      Thread thread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
          divideCountTestService.increaseErrorCount();
        }
      });
      thread.start();

      thread.join();

      // when
      long totalErrorCount = divideCountTestService.getErrorCount();

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
        divideCountTestService.increaseErrorCount();
      }

      // then
      assertThat(divideCountTestService.getErrorCount()).isEqualTo(bufferSize);
    }
  }

  @Test
  @DisplayName("카운트 초기화")
  void testReset() {
    // given
    divideCountTestService.increaseSuccessCount();
    divideCountTestService.increaseErrorCount();

    // when
    divideCountTestService.reset();

    // then
    assertAll(
        () -> assertThat(divideCountTestService.getSuccessCount()).isZero(),
        () -> assertThat(divideCountTestService.getErrorCount()).isZero()
    );
  }

}