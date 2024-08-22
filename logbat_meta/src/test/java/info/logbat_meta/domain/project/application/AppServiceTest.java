package info.logbat_meta.domain.project.application;

import info.logbat_meta.domain.project.domain.App;
import info.logbat_meta.domain.project.domain.Project;
import info.logbat_meta.domain.project.domain.enums.AppType;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat_meta.domain.project.repository.AppJpaRepository;
import info.logbat_meta.domain.project.repository.ProjectJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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
    private final String expectedProjectName = "프로젝트 이름";
    private final App expectedApp = spy(App.of(expectedProject, expectedProjectName, AppType.JAVA));

    private final Long expectedProjectId = 1L;
    private final Long expectedAppId = 1L;

    @Nested
    @DisplayName("새로운 App을 생성할 때")
    class whenCreateNewApp {

        private final String expectedAppType = AppType.JAVA.name();

        @Test
        @DisplayName("프로젝트가 존재하지 않으면 IllegalArgumentException을 던진다.")
        void willThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long notExistProjectId = 2L;
            given(projectRepository.findById(notExistProjectId)).willReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.createApp(notExistProjectId, expectedProjectName, expectedAppType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("프로젝트를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("AppType이 잘못된 경우 IllegalArgumentException을 던진다.")
        void willThrowExceptionWhenAppTypeIsInvalid() {
            // Arrange
            String invalidAppType = "INVALID";
            given(projectRepository.findById(expectedProjectId)).willReturn(
                Optional.of(expectedProject));
            // Act & Assert
            assertThatThrownBy(() -> appService.createApp(expectedProjectId, expectedProjectName, invalidAppType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 앱 타입 요청입니다.");
        }

        @Test
        @DisplayName("정상적으로 생성한다.")
        void willCreateNewApp() {
            // Arrange
            LocalDateTime expectedCreatedAt = LocalDateTime.now();
            given(projectRepository.findById(expectedProjectId)).willReturn(
                Optional.of(expectedProject));
            given(expectedProject.getId()).willReturn(expectedProjectId);
            given(appRepository.save(any(App.class))).willReturn(expectedApp);
            given(expectedApp.getId()).willReturn(expectedAppId);
            given(expectedApp.getCreatedAt()).willReturn(expectedCreatedAt);
            // Act
            AppCommonResponse actualResult = appService.createApp(expectedProjectId,
                expectedProjectName,
                expectedAppType);
            // Assert
            assertAll(
                () -> assertThat(actualResult)
                    .extracting("id", "projectId", "appType", "createdAt")
                    .containsExactly(expectedAppId, expectedProjectId, expectedAppType,
                        expectedCreatedAt),
                () -> assertThat(actualResult.getToken()).isNotNull()
            );

        }

    }

    @Nested
    @DisplayName("App을 조회할 때")
    class whenGetApp {
        private final App expectedApp = spy(App.of(expectedProject, expectedProjectName, AppType.JAVA));

        @Test
        @DisplayName("ID로 조회할 수 있다.")
        void canGetAppById() {
            // Arrange
            given(appRepository.findById(expectedAppId)).willReturn(Optional.of(expectedApp));
            given(expectedApp.getId()).willReturn(expectedAppId);
            // Act
            AppCommonResponse actualResult = appService.getAppById(expectedAppId);
            // Assert
            assertThat(actualResult)
                .extracting("id")
                .isEqualTo(expectedAppId);
        }

        @Test
        @DisplayName("Project Id로 Apps를 조회할 수 있다.")
        void canGetAppsByProjectId() {
            // Arrange
            given(appRepository.findByProject_Id(expectedProjectId)).willReturn(
                List.of(expectedApp));
            given(expectedApp.getId()).willReturn(expectedAppId);
            // Act
            List<AppCommonResponse> actualResult = appService.getAppsByProjectId(expectedProjectId);
            // Assert
            assertThat(actualResult).hasSize(1);
        }

        @Test
        @DisplayName("ID 조회시 앱을 찾을 수 없으면 예외를 던진다.")
        void willThrowExceptionWhenAppNotFoundById() {
            // Arrange
            given(appRepository.findById(expectedAppId)).willReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.getAppById(expectedAppId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("앱을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("App을 삭제할 때")
    class whenDeleteApp {

        @Test
        @DisplayName("프로젝트와 앱 ID로 삭제할 수 있다.")
        void canDeleteAppByProjectIdAndAppId() {
            // Arrange
            given(appRepository.findByProject_IdAndId(expectedProjectId, expectedAppId)).willReturn(
                Optional.of(expectedApp));
            given(expectedApp.getId()).willReturn(expectedAppId);
            // Act
            Long actualResult = appService.deleteApp(expectedProjectId, expectedAppId);
            // Assert
            assertThat(actualResult).isEqualTo(expectedAppId);
        }

        @Test
        @DisplayName("앱을 찾을 수 없으면 예외를 던진다.")
        void willThrowExceptionWhenAppNotFound() {
            // Arrange
            given(appRepository.findByProject_IdAndId(expectedProjectId, expectedAppId)).willReturn(
                Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.deleteApp(expectedProjectId, expectedAppId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("앱을 찾을 수 없습니다.");
        }
    }

}