package info.logbat.domain.logbat;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import info.logbat.exception.InvalidOptionException;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("AppKey는")
class AppKeyTest {

    private final UUID expectedAppKey = UUID.randomUUID();

    @Nested
    @DisplayName("생성 시")
    class whenCreate {

        @Test
        @DisplayName("정상적으로 생성할 수 있다.")
        void willCreateSuccess() throws InvalidOptionException {
            // Arrange
            String expectedAppKeyStr = expectedAppKey.toString();
            // Act
            AppKey actualResult = new AppKey(expectedAppKeyStr);
            // Assert
            assertThat(actualResult)
                .extracting("value")
                .isEqualTo(expectedAppKeyStr);
        }

        @ParameterizedTest
        @MethodSource("invalidAppKeyProvider")
        @DisplayName("잘못된 값인 경우 예외가 발생한다.")
        void willThrowExceptionWhenNull(String appKey, String expectedMessage) {
            // Act & Assert
            assertThatThrownBy(() -> new AppKey(appKey))
                .isInstanceOf(InvalidOptionException.class)
                .hasMessage(expectedMessage);
        }

        private static Stream<Arguments> invalidAppKeyProvider() {
            return Stream.of(
                Arguments.of(null, "AppKey must not be null or empty."),
                Arguments.of("", "AppKey must not be null or empty."),
                Arguments.of("      ", "AppKey must not be null or empty."),
                Arguments.of("invalid", "AppKey must be a valid UUID string.")
            );
        }
    }

}
