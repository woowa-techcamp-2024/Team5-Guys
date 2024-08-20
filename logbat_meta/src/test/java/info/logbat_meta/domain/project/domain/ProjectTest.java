package info.logbat_meta.domain.project.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Project 도메인은")
class ProjectTest {

    @DisplayName("생성시")
    @Nested
    class whenCreated {

        @DisplayName("이름이 잘못된 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("exceptionValues")
        void throwsExceptionForInvalidName(String name) {
            // Act & Assert
            assertThatThrownBy(() -> Project.from(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 이름 요청입니다.");
        }

        @Test
        @DisplayName("정상적으로 생성된다.")
        void testFromMethodSuccess() {
            // Arrange
            String expectedProjectName = "이름";
            // Act
            Project actualResult = Project.from(expectedProjectName);
            // Assert
            assertThat(actualResult)
                .hasFieldOrPropertyWithValue("name", expectedProjectName);
        }

        private static Stream<Arguments> exceptionValues() {
            return Stream.of(
                Arguments.of(""),
                Arguments.of((String) null),
                Arguments.of("   "),
                Arguments.of("한글33자이상이면예외가발생해야할것같은데진짜발생하는지확인하기위한테스트값입니다.")
            );
        }

    }

    @DisplayName("수정시")
    @Nested
    class whenUpdated {

        @DisplayName("이름이 잘못된 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("exceptionValues")
        void throwsExceptionForInvalidName(String name) {
            // Arrange
            Project project = Project.from("이름");
            // Act & Assert
            assertThatThrownBy(() -> project.updateName(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 이름 요청입니다.");
        }

        @Test
        @DisplayName("정상적으로 수정된다.")
        void testUpdateNameMethodSuccess() {
            // Arrange
            Project project = Project.from("이름");
            String expectedProjectName = "수정된 이름";
            // Act
            project.updateName(expectedProjectName);
            // Assert
            assertThat(project)
                .hasFieldOrPropertyWithValue("name", expectedProjectName);
        }

        private static Stream<Arguments> exceptionValues() {
            return Stream.of(
                Arguments.of(""),
                Arguments.of((String) null),
                Arguments.of("   "),
                Arguments.of("한글33자이상이면예외가발생해야할것같은데진짜발생하는지확인하기위한테스트값입니다.")
            );
        }
    }


}
