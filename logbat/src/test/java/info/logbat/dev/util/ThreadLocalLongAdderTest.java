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

@DisplayName("ThreadLocalLongAdder는")
class ThreadLocalLongAdderTest {

    private final ThreadLocalLongAdder adder = new ThreadLocalLongAdder();

    @AfterEach
    void tearDown() {
        adder.reset();
    }

    @Test
    @DisplayName("초기값을 0으로 설정한다.")
    void initialValueIsZero() {
        assertThat(adder.get()).isZero();
    }

    @Nested
    @DisplayName("값을 증가시킬 때")
    class WhenIncrementing {

        @Test
        @DisplayName("단일 호출 시 값이 1 증가한다")
        void increasesValueByOne() {
            // Arrange
            adder.increment();
            // Act & Assert
            assertThat(adder.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("다중 호출 시 호출한 만큼 값이 증가한다")
        void multipleIncrementsIncreaseValue() {
            // Arrange
            int count = 5;
            for (int i = 0; i < count; i++) {
                adder.increment();
            }
            // Act & Assert
            assertThat(adder.get()).isEqualTo(count);
        }
    }

    @Test
    @DisplayName("Reset시 0으로 초기화된다")
    void resetSetsValueToZero() {
        // Arrange
        adder.increment();
        // Act
        adder.reset();
        // Assert
        assertThat(adder.get()).isZero();
    }

    @Test
    @DisplayName("두 개의 스레드에서 증가시킨 값의 합을 정확히 반환한다")
    void returnsSumOfTwoThreads() throws InterruptedException {
        // Arrange
        int expectedIncrementsPerThread = 10;
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < expectedIncrementsPerThread; i++) {
                adder.increment();
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < expectedIncrementsPerThread; i++) {
                adder.increment();
            }
        });

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        long actualResult = adder.get();

        // Assert
        assertThat(actualResult).isEqualTo(expectedIncrementsPerThread * 2);
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

}
