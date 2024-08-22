package info.logbat.domain.logbat;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import info.logbat.exception.InvalidAppKeyException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AppKey 테스트")
class AppKeyTest {

    @DisplayName("AppKey를 생성할 수 있다.")
    @Test
    void createAppKey() throws InvalidAppKeyException {
        // given
        String appKeyStr = UUID.randomUUID().toString();

        // when
        AppKey appKey = new AppKey(appKeyStr);

        // then
        assertThat(appKey)
            .extracting(AppKey::getValue)
            .isEqualTo(appKeyStr);
    }

    @DisplayName("AppKey 생성 시 null인 경우 예외가 발생한다.")
    @Test
    void createAppKeyWithNull() {
        // given
        String appKeyStr = null;

        // when & then
        assertThatThrownBy(() -> new AppKey(appKeyStr))
            .isInstanceOf(InvalidAppKeyException.class)
            .hasMessage("AppKey must not be null or empty.");
    }

    @DisplayName("AppKey 생성 시 빈 문자열인 경우 예외가 발생한다.")
    @Test
    void createAppKeyWithEmpty() {
        // given
        String appKeyStr = "";

        // when & then
        assertThatThrownBy(() -> new AppKey(appKeyStr))
            .isInstanceOf(InvalidAppKeyException.class)
            .hasMessage("AppKey must not be null or empty.");
    }

    @DisplayName("AppKey 생성 시 공백 문자열인 경우 예외가 발생한다.")
    @Test
    void createAppKeyWithBlank() {
        // given
        String appKeyStr = " ";

        // when & then
        assertThatThrownBy(() -> new AppKey(appKeyStr))
            .isInstanceOf(InvalidAppKeyException.class)
            .hasMessage("AppKey must not be null or empty.");
    }

    @DisplayName("AppKey 생성 시 UUID 형식이 아닌 경우 예외가 발생한다.")
    @Test
    void createAppKeyWithInvalidUuidString() {
        // given
        String appKeyStr = "invalidUuidString";

        // when & then
        assertThatThrownBy(() -> new AppKey(appKeyStr))
            .isInstanceOf(InvalidAppKeyException.class)
            .hasMessage("AppKey must be a valid UUID string.");
    }

}