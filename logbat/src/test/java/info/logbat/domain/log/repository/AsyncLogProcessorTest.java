package info.logbat.domain.log.repository;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.domain.log.domain.Log;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AsyncLogProcessor 기능 테스트")
class AsyncLogProcessorTest {

    private AsyncLogProcessor asyncLogProcessor;
    private AtomicInteger processedLogCount;
    private CountDownLatch latch;

    @BeforeEach
    void setUp() {
        asyncLogProcessor = new AsyncLogProcessor();
        processedLogCount = new AtomicInteger(0);
    }

    @DisplayName("단일 로그 제출 및 처리 확인")
    @Test
    void testSubmitSingleLog() throws InterruptedException {
        // given
        latch = new CountDownLatch(1);
        asyncLogProcessor.init(logs -> {
            processedLogCount.addAndGet(logs.size());
            latch.countDown();
        });

        Log log = new Log(1L, UUID.randomUUID().toString(), 0, "Test log",
            LocalDateTime.of(2021, 1, 1, 0, 0, 0));

        // when
        asyncLogProcessor.submitLog(log);

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(processedLogCount.get())
            .isEqualTo(1);
    }

    @DisplayName("벌크 로그 제출 및 처리 확인")
    @Test
    void testSubmitBulkLogs() throws InterruptedException {
        // given
        int logCount = 150; // DEFAULT_BULK_SIZE(100)보다 큰 값
        latch = new CountDownLatch(2); // 최소 2번의 처리를 기대
        asyncLogProcessor.init(logs -> {
            processedLogCount.addAndGet(logs.size());
            latch.countDown();
        });

        // when
        for (int i = 0; i < logCount; i++) {
            asyncLogProcessor.submitLog(
                new Log((long) i, UUID.randomUUID().toString(), 0, "Test log " + i,
                    LocalDateTime.of(2021, 1, 1, 0, 0, 0)));
        }

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(processedLogCount.get()).isEqualTo(logCount);
    }

}