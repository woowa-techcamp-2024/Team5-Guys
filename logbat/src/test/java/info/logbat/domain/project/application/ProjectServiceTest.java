package info.logbat.domain.project.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.presentation.payload.response.ProjectCommonResponse;
import info.logbat.domain.project.repository.ProjectJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService는")
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectJpaRepository projectRepository;

    private final Long expectedProjectId = 1L;
    private final String expectedProjectName = "프로젝트 이름";
    private final LocalDateTime expectedCreatedAt = LocalDateTime.now();

    @Test
    @DisplayName("새로운 프로젝트를 만들 수 있다.")
    void canCreateProject() {
        // Arrange

        Project expectedProject = spy(Project.from(expectedProjectName));
        given(projectRepository.save(any(Project.class))).willReturn(expectedProject);
        given(expectedProject.getId()).willReturn(expectedProjectId);
        given(expectedProject.getCreatedAt()).willReturn(expectedCreatedAt);
        // Act
        ProjectCommonResponse actualResult = projectService.createProject(expectedProjectName);
        // Assert
        assertAll(
            () -> assertThat(actualResult)
                .extracting("id", "name", "createdAt")
                .containsExactly(expectedProjectId, expectedProjectName, expectedCreatedAt),
            () -> assertThat(actualResult.updatedAt()).isNull()
        );
    }

    @Nested
    @DisplayName("프로젝트를 조회할 때")
    class whenGetProject {

        @Test
        @DisplayName("프로젝트 이름으로 조회할 수 있다.")
        void canGetProjectByName() {
            // Arrange
            Project expectedProject = spy(Project.from(expectedProjectName));
            given(projectRepository.findByName(expectedProjectName)).willReturn(
                Optional.of(expectedProject));
            given(expectedProject.getId()).willReturn(expectedProjectId);
            given(expectedProject.getCreatedAt()).willReturn(expectedCreatedAt);
            // Act
            ProjectCommonResponse actualResult = projectService.getProjectByName(
                expectedProjectName);
            // Assert
            assertAll(
                () -> assertThat(actualResult)
                    .extracting("id", "name", "createdAt")
                    .containsExactly(expectedProjectId, expectedProjectName, expectedCreatedAt),
                () -> assertThat(actualResult.updatedAt()).isNull()
            );
        }

        @Test
        @DisplayName("프로젝트 이름으로 조회할 수 없다면 IllegalArgumentException을 던진다.")
        void cannotGetProjectByName() {
            // Arrange
            String expectedWrongProjectName = "없는 프로젝트 이름";
            given(projectRepository.findByName(expectedWrongProjectName)).willReturn(
                Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> projectService.getProjectByName(expectedWrongProjectName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로젝트를 찾을 수 없습니다.");
        }
    }

}