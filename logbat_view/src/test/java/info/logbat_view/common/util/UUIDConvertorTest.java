package info.logbat_view.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("UUIDConvertor는")
class UUIDConvertorTest {

    @Nested
    @DisplayName("문자열을 UUID로 변환할 때")
    class whenConvertStringToUUID {

        @Test
        @DisplayName("정상적으로 변환한다.")
        void willReturnSuccess() {
            // Arrange
            UUID expectedUUID = UUID.randomUUID();
            // Act
            UUID actualUUID = UUIDConvertor.convertStringToUUID(expectedUUID.toString());
            // Assert
            assertThat(actualUUID).isEqualTo(expectedUUID);
        }

        @ParameterizedTest
        @MethodSource("exceptionValues")
        @DisplayName("문자열이 null이거나 빈 문자열일 경우 예외를 던진다.")
        void willThrowExceptionWhenStringIsNull(String uuid) {
            // Act & Assert
            assertThatThrownBy(() -> UUIDConvertor.convertStringToUUID(uuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID는 필수 값 입니다.");
        }

        @Test
        @DisplayName("올바르지 않은 UUID 형식일 경우 예외를 던진다.")
        void willThrowExceptionWhenStringIsNotUUID() {
            // Arrange
            String invalidUUID = "invalid-uuid";
            // Act & Assert
            assertThatThrownBy(() -> UUIDConvertor.convertStringToUUID(invalidUUID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UUID 형식이 올바르지 않습니다.");
        }

        private static Stream<Arguments> exceptionValues() {
            return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of(" ")
            );
        }
    }
}