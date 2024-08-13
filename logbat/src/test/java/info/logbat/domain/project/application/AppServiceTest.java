package info.logbat.domain.project.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import info.logbat.domain.project.domain.App;
import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.domain.enums.AppType;
import info.logbat.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat.domain.project.repository.AppJpaRepository;
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
@DisplayName("AppService는")
class AppServiceTest {

    @InjectMocks
    private AppService appService;

    @Mock
    private AppJpaRepository appRepository;
    @Mock
    private ProjectJpaRepository projectRepository;

    private final Project expectedProject = mock(Project.class);
    private final App expectedApp = spy(App.of(expectedProject, AppType.JAVA));
    private final Long expectedProjectId = 1L;

    @Nested
    @DisplayName("새로운 App을 생성할 때")
    class whenCreateNewApp {

        @Test
        @DisplayName("프로젝트가 존재하지 않으면 IllegalArgumentException을 던진다.")
        void willThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long notExistProjectId = 2L;
            given(projectRepository.findById(notExistProjectId)).willReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.createApp(notExistProjectId, AppType.JAVA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로젝트를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("정상적으로 생성한다.")
        void willCreateNewApp() {
            // Arrange
            Long expectedAppId = 1L;
            LocalDateTime expectedCreatedAt = LocalDateTime.now();
            given(projectRepository.findById(expectedProjectId)).willReturn(
                Optional.of(expectedProject));
            given(expectedProject.getId()).willReturn(expectedProjectId);
            given(appRepository.save(any(App.class))).willReturn(expectedApp);
            given(expectedApp.getId()).willReturn(expectedAppId);
            given(expectedApp.getCreatedAt()).willReturn(expectedCreatedAt);
            // Act
            AppCommonResponse actualResult = appService.createApp(expectedProjectId, AppType.JAVA);
            // Assert
            assertAll(
                () -> assertThat(actualResult)
                    .extracting("id", "projectId", "appType", "createdAt")
                    .containsExactly(expectedAppId, expectedProjectId, AppType.JAVA.name(),
                        expectedCreatedAt),
                () -> assertThat(actualResult.token()).isNotNull()
            );

        }

    }

}