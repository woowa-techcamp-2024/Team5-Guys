package info.logbat.domain.log.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

@Deprecated(forRemoval = true)
@Disabled
@DisplayName("AsyncLogProcessor는")
class AsyncLogProcessorTest {

//    private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
//    private final HikariDataSource hikariDataSource = Mockito.mock(HikariDataSource.class);
//    private final Long expectedLogId = 1L;
//    private final LocalDateTime expectedLogTimestamp = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
//    private final Log expectedLog = new Log(1L, expectedLogId, 0, "Test log", expectedLogTimestamp);
//
//    private AsyncLogProcessor asyncLogProcessor;
//    private AtomicInteger processedLogCount;
//    private CountDownLatch latch;
//
//    @BeforeEach
//    void setUp() {
//        // HikariDataSource 모킹
//        when(jdbcTemplate.getDataSource()).thenReturn(hikariDataSource);
//        when(hikariDataSource.getMaximumPoolSize()).thenReturn(10); // 원하는 풀 사이즈 설정
//
//        asyncLogProcessor = new AsyncLogProcessor(2000L, 100, jdbcTemplate);
//        processedLogCount = new AtomicInteger(0);
//    }
//
//    @Test
//    @DisplayName("단일 로그를 처리할 수 있다.")
//    void testSubmitSingleLog() throws InterruptedException {
//        // Arrange
//        latch = new CountDownLatch(1);
//        asyncLogProcessor.init(logs -> {
//            processedLogCount.addAndGet(logs.size());
//            latch.countDown();
//        });
//
//        // Act
//        asyncLogProcessor.submitLog(expectedLog);
//
//        // Assert
//        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
//        assertThat(processedLogCount.get())
//            .isEqualTo(1);
//    }
//
//    @DisplayName("벌크 로그를 처리할 수 있다.")
//    @Test
//    void testSubmitBulkLogs() throws InterruptedException {
//        // Arrange
//        int logCount = 150; // DEFAULT_BULK_SIZE(100)보다 큰 값
//        latch = new CountDownLatch(2); // 최소 2번의 처리를 기대
//        asyncLogProcessor.init(logs -> {
//            processedLogCount.addAndGet(logs.size());
//            latch.countDown();
//        });
//        // Act
//        for (int i = 0; i < logCount; i++) {
//            asyncLogProcessor.submitLog(
//                new Log((long) i, expectedLogId, 0, "Test log " + i, expectedLogTimestamp));
//        }
//        // Assert
//        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
//        assertThat(processedLogCount.get()).isEqualTo(logCount);
//    }

}
