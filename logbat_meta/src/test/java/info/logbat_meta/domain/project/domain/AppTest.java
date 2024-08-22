package info.logbat_meta.domain.project.domain;

import info.logbat_meta.domain.project.domain.enums.AppType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("App 도메인은")
class AppTest {

    @Nested
    @DisplayName("생성 시")
    class whenCreated {

        private static final Project EXPECTED_PROJECT = Project.from("프로젝트");
        private static final String EXPECTED_APP_NAME = "앱 이름";

        @Test
        @DisplayName("정상적으로 생성된다.")
        void createSuccess() {
            // Arrange
            AppType expectedAppType = AppType.JS;
            // Act
            App actualResult = App.of(EXPECTED_PROJECT, EXPECTED_APP_NAME, expectedAppType);
            // Assert
            assertAll(
                () -> assertThat(actualResult)
                    .isNotNull()
                    .extracting("project", "appType")
                    .containsExactly(EXPECTED_PROJECT, expectedAppType),
                () -> assertThat(actualResult.getAppKey()).isNotNull()
            );
        }

        @ParameterizedTest
        @MethodSource("exceptionProvider")
        @DisplayName("인자가 없으면 예외가 발생한다.")
        void createFail(Project project, AppType appType, String expectedMessage) {
            // Act & Assert
            assertThatThrownBy(() -> App.of(project, EXPECTED_APP_NAME, appType))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(expectedMessage);
        }

        private static Stream<Arguments> exceptionProvider() {
            return Stream.of(
                Arguments.of(null, AppType.JS, "프로젝트는 필수입니다."),
                Arguments.of(EXPECTED_PROJECT, null, "앱 타입은 필수입니다.")
            );
        }
    }
}