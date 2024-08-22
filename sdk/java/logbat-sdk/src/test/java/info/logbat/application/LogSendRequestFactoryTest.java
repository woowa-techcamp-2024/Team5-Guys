package info.logbat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import info.logbat.infrastructure.payload.LogSendRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("LogSendRequestFactory는 ILoggingEvent를 LogSendRequest로 변환할 수 있다.")
class LogSendRequestFactoryTest {

    private LogSendRequestFactory factory;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        factory = new LogSendRequestFactory();
    }

    @ParameterizedTest(name = "로그 레벨이 {0}일 때 {1}로 변환된다.")
    @MethodSource("provideLevels")
    @DisplayName("다양한 로그 레벨에 대한 변환을 테스트한다.")
    void testDifferentLogLevels(Level inputLevel, String expectedLevel) {
        // given
        ILoggingEvent event = createMockEvent(inputLevel, "Test message", new HashMap<>(), null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.level()).isEqualTo(expectedLevel);
        assertThat(result.data()).contains(expectedLevel);
    }

    @Test
    @DisplayName("복잡한 로깅 이벤트를 올바르게 변환할 수 있다.")
    void testComplexLoggingEvent() {
        // given
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLevel()).thenReturn(Level.ERROR);
        when(event.getTimeStamp()).thenReturn(1629456789000L); // 2021-08-20 12:33:09 UTC
        when(event.getThreadName()).thenReturn("main-thread");
        when(event.getLoggerName()).thenReturn("com.example.ComplexLogger");
        when(event.getFormattedMessage()).thenReturn("A complex error occurred");

        Map<String, String> mdcMap = new HashMap<>();
        mdcMap.put("userId", "user123");
        mdcMap.put("requestId", "req456");
        mdcMap.put("sessionId", "session789");
        when(event.getMDCPropertyMap()).thenReturn(mdcMap);

        ThrowableProxy throwableProxy = mock(ThrowableProxy.class);
        when(throwableProxy.getMessage()).thenReturn("NullPointerException: Object is null");
        when(event.getThrowableProxy()).thenReturn(throwableProxy);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        String expectedTimestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(1629456789000L),
            ZoneId.systemDefault()
        ).format(formatter);
        assertThat(result.level()).isEqualTo("ERROR");
        assertThat(result.timestamp()).isEqualTo(expectedTimestamp);
        assertThat(result.data())
            .contains("[main-thread]")
            .contains("[com.example.ComplexLogger]")
            .contains("ERROR")
            .contains("A complex error occurred")
            .contains("MDC: {")
            .contains("userId=user123")
            .contains("requestId=req456")
            .contains("sessionId=session789")
            .contains("EXCEPTION: NullPointerException: Object is null");
    }

    @Test
    @DisplayName("최소한의 정보만 있는 로깅 이벤트를 올바르게 변환할 수 있다.")
    void testMinimalLoggingEvent() {
        // given
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(event.getTimeStamp()).thenReturn(1629456789000L); // 2021-08-20 12:33:09 UTC
        when(event.getThreadName()).thenReturn("thread-1");
        when(event.getLoggerName()).thenReturn("com.example.MinimalLogger");
        when(event.getFormattedMessage()).thenReturn("A simple log message");
        when(event.getMDCPropertyMap()).thenReturn(new HashMap<>());
        when(event.getThrowableProxy()).thenReturn(null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        String expectedTimestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(1629456789000L),
            ZoneId.systemDefault()
        ).format(formatter);

        assertThat(result.level()).isEqualTo("INFO");
        assertThat(result.timestamp()).isEqualTo(expectedTimestamp);
        assertThat(result.data())
            .contains("[thread-1]")
            .contains("[com.example.MinimalLogger]")
            .contains("INFO")
            .contains("A simple log message")
            .doesNotContain("MDC:")
            .doesNotContain("EXCEPTION:");
    }

    static Stream<Arguments> provideLevels() {
        return Stream.of(
            Arguments.of(Level.TRACE, "TRACE"),
            Arguments.of(Level.DEBUG, "DEBUG"),
            Arguments.of(Level.INFO, "INFO"),
            Arguments.of(Level.WARN, "WARN"),
            Arguments.of(Level.ERROR, "ERROR"),
            Arguments.of(Level.ALL, "DEBUG"),  // 지원하지 않는 레벨
            Arguments.of(Level.OFF, "DEBUG")   // 지원하지 않는 레벨
        );
    }

    @Test
    @DisplayName("MDC 정보가 없는 경우 적절히 처리된다.")
    void testEmptyMdc() {
        // given
        ILoggingEvent event = createMockEvent(Level.INFO, "Test message", new HashMap<>(), null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data()).doesNotContain("MDC:");
    }

    @Test
    @DisplayName("MDC 정보가 있는 경우 적절히 처리된다.")
    void testNonEmptyMdc() {
        // given
        Map<String, String> mdcMap = new HashMap<>();
        mdcMap.put("userId", "12345");
        mdcMap.put("requestId", "abcde");
        ILoggingEvent event = createMockEvent(Level.INFO, "Test message", mdcMap, null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data())
            .contains("MDC: {")
            .contains("userId=12345")
            .contains("requestId=abcde");
    }

    @ParameterizedTest(name = "예외 정보가 {0} 경우 적절히 처리된다.")
    @MethodSource("provideExceptionCases")
    @DisplayName("예외 정보 유무에 따른 변환을 테스트한다.")
    void testExceptionHandling(String exceptionCase, ThrowableProxy throwableProxy,
        String expectedExceptionContent) {
        // given
        ILoggingEvent event = createMockEvent(Level.ERROR, "Test message", new HashMap<>(),
            throwableProxy);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        if (expectedExceptionContent.isEmpty()) {
            assertThat(result.data()).doesNotContain("EXCEPTION:");
        } else {
            assertThat(result.data()).contains(expectedExceptionContent);
        }
    }

    static Stream<Arguments> provideExceptionCases() {
        ThrowableProxy nonNullProxy = mock(ThrowableProxy.class);
        when(nonNullProxy.getMessage()).thenReturn("Test exception");

        return Stream.of(
            Arguments.of("없는", null, ""),
            Arguments.of("있는", nonNullProxy, "EXCEPTION: Test exception")
        );
    }

    private ILoggingEvent createMockEvent(Level level, String message, Map<String, String> mdcMap,
        ThrowableProxy throwableProxy) {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLevel()).thenReturn(level);
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getThreadName()).thenReturn("testThread");
        when(event.getLoggerName()).thenReturn("com.example.TestLogger");
        when(event.getFormattedMessage()).thenReturn(message);
        when(event.getMDCPropertyMap()).thenReturn(mdcMap);
        when(event.getThrowableProxy()).thenReturn(throwableProxy);
        return event;
    }

    @Test
    @DisplayName("MDC에 특수 문자가 포함된 경우 적절히 처리된다.")
    void testMdcWithSpecialCharacters() {
        // given
        Map<String, String> mdcMap = new HashMap<>();
        mdcMap.put("key with spaces", "value with spaces");
        mdcMap.put("key=with=equals", "value=with=equals");
        ILoggingEvent event = createMockEvent(Level.INFO, "Test message", mdcMap, null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data())
            .contains("MDC: {")
            .contains("key with spaces=value with spaces")
            .contains("key=with=equals=value=with=equals");
    }

    @Test
    @DisplayName("예외 메시지에 특수 문자가 포함된 경우 적절히 처리된다.")
    void testExceptionWithSpecialCharacters() {
        // given
        ThrowableProxy throwableProxy = mock(ThrowableProxy.class);
        when(throwableProxy.getMessage()).thenReturn("Error: Invalid input [x < y]");
        ILoggingEvent event = createMockEvent(Level.ERROR, "Test message", new HashMap<>(),
            throwableProxy);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data()).contains("EXCEPTION: Error: Invalid input [x < y]");
    }

    @Test
    @DisplayName("로그 메시지에 줄바꿈이 포함된 경우 적절히 처리된다.")
    void testMultilineLogMessage() {
        // given
        String multilineMessage = "First line\nSecond line\nThird line";
        ILoggingEvent event = createMockEvent(Level.INFO, multilineMessage, new HashMap<>(), null);

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data())
            .contains("First line")
            .contains("Second line")
            .contains("Third line");
    }

    @Test
    @DisplayName("로거 이름이 null인 경우 적절히 처리된다.")
    void testNullLoggerName() {
        // given
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getThreadName()).thenReturn("testThread");
        when(event.getLoggerName()).thenReturn(null);
        when(event.getFormattedMessage()).thenReturn("Test message");
        when(event.getMDCPropertyMap()).thenReturn(new HashMap<>());

        // when
        LogSendRequest result = factory.createLogSendRequest(event);

        // then
        assertThat(result.data()).contains("[testThread] [unknown] INFO - Test message");
    }

}