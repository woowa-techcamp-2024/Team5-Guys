package info.logbat.domain.options;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.config.LogBatConfig;
import info.logbat.exception.InvalidOptionException;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogBatOptions는")
class LogBatOptionsTest {

    @Test
    @DisplayName("Config을 전달받아 값을 저장한다.")
    void saveOptionValuesByConfig() throws InvalidOptionException {
        // Arrange
        String expectedAppKey = UUID.randomUUID().toString();
        LogBatConfig logbatConfig = new LogBatConfig(Map.of("appKey", expectedAppKey));
        // Act
        LogBatOptions actualResult = new LogBatOptions(logbatConfig);
        // Assert
        assertThat(actualResult)
            .extracting("appKey")
            .extracting("value")
            .isEqualTo(expectedAppKey);
    }

}
