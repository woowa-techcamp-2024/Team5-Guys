package info.logbat.dev.util;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("ThreadLocalLongAdder 테스트")
class ThreadLocalLongAdderTest {

  private ThreadLocalLongAdder adder = new ThreadLocalLongAdder();

  @AfterEach
  void tearDown() {
    adder.reset();
  }

  @Test
  @DisplayName("초기값은 0이다")
  void initialValueIsZero() {
    assertThat(adder.get()).isZero();
  }

  @Nested
  @DisplayName("increment 테스트")
  class IncrementTest {

    @Test
    @DisplayName("increment 호출 시 값이 1 증가한다")
    void incrementIncreasesValueByOne() {
      // when
      adder.increment();

      // then
      assertThat(adder.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 번 increment 호출 시 호출한 만큼 값이 증가한다")
    void multipleIncrementsIncreaseValue() {
      // when
      int count = 5;
      for (int i = 0; i < count; i++) {
        adder.increment();
      }

      // then
      assertThat(adder.get()).isEqualTo(count);
    }
  }

  @Test
  @DisplayName("reset 호출 시 값이 0으로 초기화된다")
  void resetSetsValueToZero() {
    // given
    adder.increment();
    assertThat(adder.get()).isNotZero();

    // when
    adder.reset();

    // then
    assertThat(adder.get()).isZero();
  }

  @Test
  @DisplayName("두 개의 스레드에서 증가시킨 값의 합을 정확히 반환한다")
  void returnsSumOfTwoThreads() throws InterruptedException {
    // given
    int incrementsPerThread = 10;

    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < incrementsPerThread; i++) {
        adder.increment();
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < incrementsPerThread; i++) {
        adder.increment();
      }
    });

    // when
    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();

    long result = adder.get();

    // then
    assertThat(result).isEqualTo(incrementsPerThread * 2);
  }

  @Test
  @Disabled("부하가 큰 테스트이므로 평소에는 비활성화")
  @DisplayName("여러 스레드에서 동시에 증가시켜도 모든 스레드의 합을 정확히 반환한다")
  void returnsSumOfMultipleThreads() throws InterruptedException {
    int threadCount = 1000;
    int incrementsPerThread = 10000000;

    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    List<Callable<Void>> tasks = new ArrayList<>(threadCount);
    for (int i = 0; i < threadCount; i++) {
      tasks.add(() -> {
        for (int j = 0; j < incrementsPerThread; j++) {
          adder.increment();
        }
        return null;
      });
    }

    executorService.invokeAll(tasks);
    executorService.shutdown();

    long result = adder.get();

    assertThat(result).isEqualTo((long) threadCount * incrementsPerThread);
  }

  @Test
  @DisplayName("reset 메소드는 누적 합계를 0으로 초기화한다")
  void resetMethodClearsCumulativeSum() {
    // given
    adder.increment();
    adder.get();  // 누적값 1
    adder.increment();
    adder.get();  // 누적값 3

    // when
    adder.reset();
    long result = adder.get();

    // then
    assertThat(result).isZero();
  }
}