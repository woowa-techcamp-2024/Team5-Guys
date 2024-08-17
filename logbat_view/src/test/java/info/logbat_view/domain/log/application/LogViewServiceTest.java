package info.logbat_view.domain.log.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import info.logbat_view.common.util.UUIDConvertor;
import info.logbat_view.domain.log.domain.Log;
import info.logbat_view.domain.log.domain.LogData;
import info.logbat_view.domain.log.domain.enums.LogLevel;
import info.logbat_view.domain.log.domain.service.LogService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogViewService는")
class LogViewServiceTest {

    @InjectMocks
    private LogViewService logViewService;

    @Mock
    private LogService logService;

    private final Long expectedId = 1L;
    private final UUID expectedUUID = UUID.randomUUID();
    private final byte[] expectedAppKey = UUIDConvertor.convertUUIDToBytes(expectedUUID);
    private final LogLevel expectedLevel = LogLevel.INFO;
    private final String expectedData = "data";
    private final LocalDateTime expectedTimestamp = LocalDateTime.of(2024, 8, 15, 12, 0, 0, 0);
    private final Log expectedLog = Log.from(
        new LogData(expectedId, expectedAppKey, expectedLevel.name(), expectedData,
            expectedTimestamp));


    @Test
    @DisplayName("appkey로 로그를 조회한다.")
    void findLogsByAppKey() {
        // Arrange
        given(logService.findLogsByAppKey(any(UUID.class), any(Long.class),
            any(Integer.class))).willReturn(Flux.just(expectedLog));
        // Act & Assert
        logViewService.findLogs(expectedUUID.toString(), -1L, 10)
            .as(StepVerifier::create)
            .expectNextMatches(response -> {
                assertThat(response)
                    .extracting("id", "level", "data", "timestamp")
                    .containsExactly(expectedId, expectedLevel.name(), expectedData,
                        expectedTimestamp);
                return true;
            })
            .verifyComplete();
    }

}