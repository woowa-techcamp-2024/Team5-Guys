package info.logbat.logback.domain.logbat;

import static org.assertj.core.api.Assertions.assertThat;

import info.logbat.logback.config.LogbatConfig;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogbatOptionsTest {

    @DisplayName("LogbatOption을 생성할 수 있다.")
    @Test
    void createLogbatOptions() {
        // given
        String appKeyStr = UUID.randomUUID().toString();
        LogbatConfig logbatConfig = new LogbatConfig(appKeyStr);

        // when
        LogbatOptions logbatOptions = new LogbatOptions(logbatConfig);

        // then
        assertThat(logbatOptions)
            .extracting("appKey.value")
            .isEqualTo(appKeyStr);
    }

}