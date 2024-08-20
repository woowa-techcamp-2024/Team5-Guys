package info.logbat_meta.domain.project.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AppType Enum은")
class AppTypeTest {

    @ParameterizedTest
    @MethodSource("nameProvider")
    @DisplayName("이름으로 Enum을 찾을 수 있다.")
    void findEnumByName(String name, AppType expectedResult) {
        // Act
        AppType actualResult = AppType.from(name);
        // Assert
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    @DisplayName("잘못된 이름으로 Enum을 찾을 경우 예외를 발생시킨다.")
    void throwExceptionWhenFindEnumByInvalidName(String name) {
        // Act & Assert
        assertThatThrownBy(() -> AppType.from(name))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잘못된 앱 타입 요청입니다.");
    }

    private static Stream<Arguments> nameProvider() {
        return Stream.of(
            Arguments.of("JS", AppType.JS),
            Arguments.of("JAVA", AppType.JAVA)
        );
    }

    private static Stream<String> exceptionProvider() {
        return Stream.of("INVALID", "C++", "PYTHON", null);
    }

}
