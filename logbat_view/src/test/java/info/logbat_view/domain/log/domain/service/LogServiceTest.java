package info.logbat_view.domain.log.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import info.logbat_view.common.util.UUIDConvertor;
import info.logbat_view.domain.log.domain.LogData;
import info.logbat_view.domain.log.domain.enums.LogLevel;
import info.logbat_view.domain.log.repository.LogDataRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogService는")
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Mock
    private LogDataRepository logDataRepository;

    private final Long expectedLogDataId = 1L;
    private final UUID expectedUUID = UUID.randomUUID();
    private final byte[] expectedAppKey = UUIDConvertor.convertUUIDToBytes(expectedUUID);
    private final LogLevel expectedLogLevel = LogLevel.ERROR;
    private final String expectedData = "data";
    private final LocalDateTime expectedTimestamp = LocalDateTime.of(2024, 8, 15, 12, 0, 0, 0);
    private final LogData expectedLogData = new LogData(expectedLogDataId, expectedAppKey,
        expectedLogLevel.getValue(), expectedData, expectedTimestamp);

    @Nested
    @DisplayName("AppKey로 Log들을 조회할 때")
    class whenGetAppsByAppKey {

        @Test
        @DisplayName("조회된 LogData들을 Log로 매핑해서 반환한다.")
        void shouldReturnApps() {
            // Arrange
            given(logDataRepository.findByAppKeyAndLogIdGreaterThanOrderByLogId(any(byte[].class),
                any(Long.class))).willReturn(
                Flux.just(expectedLogData));
            // Act & Assert
            StepVerifier.create(logService.findLogsByAppKey(expectedUUID, 0L, 2))
                .expectNextMatches(log -> {
                    assertThat(log).extracting("id", "level", "data", "timestamp")
                        .containsExactly(expectedLogDataId, expectedLogLevel, expectedData,
                            expectedTimestamp);
                    return true;
                })
                .verifyComplete();
        }

    }
}