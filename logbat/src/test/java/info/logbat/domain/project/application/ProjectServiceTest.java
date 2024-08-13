package info.logbat.domain.project.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.presentation.payload.response.ProjectCreateResponse;
import info.logbat.domain.project.repository.ProjectJpaRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("새로운 프로젝트를 만들 수 있다.")
    void canCreateProject() {
        // Arrange
        Long expectedProjectId = 1L;
        String expectedProjectName = "프로젝트 이름";
        LocalDateTime expectedCreatedAt = LocalDateTime.now();
        Project expectedProject = spy(Project.from(expectedProjectName));
        given(projectRepository.save(any(Project.class))).willReturn(expectedProject);
        given(expectedProject.getId()).willReturn(expectedProjectId);
        given(expectedProject.getCreatedAt()).willReturn(expectedCreatedAt);
        // Act
        ProjectCreateResponse actualResult = projectService.createProject(expectedProjectName);
        // Assert
        assertThat(actualResult)
            .extracting("id", "name", "createdAt")
            .containsExactly(expectedProjectId, expectedProjectName, expectedCreatedAt);
    }

}