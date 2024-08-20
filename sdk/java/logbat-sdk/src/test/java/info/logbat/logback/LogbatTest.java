package info.logbat.logback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogbatTest {

    @DisplayName("Logbat을 생성할 수 있다.")
    @Test
    void createLogbat() {
        // given
        String appKeyStr = "testAppKey";

        // when
        Logbat logbat = new Logbat(appKeyStr);

        // then
        assertThat(logbat)
            .extracting("appKey.value")
            .isEqualTo(appKeyStr);
    }
}