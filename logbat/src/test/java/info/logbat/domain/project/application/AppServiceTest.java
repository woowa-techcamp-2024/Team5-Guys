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
import java.util.UUID;
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
    private final Long expectedAppId = 1L;

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

    @Nested
    @DisplayName("App을 조회할 때")
    class whenGetApp {

        private final UUID expectedToken = UUID.randomUUID();
        private final App expectedApp = spy(App.of(expectedProject, AppType.JAVA));

        @Test
        @DisplayName("토큰으로 조회할 수 있다.")
        void canGetAppByToken() {
            // Arrange
            String expectedTokenString = expectedToken.toString();
            given(appRepository.findByToken(expectedToken)).willReturn(Optional.of(expectedApp));
            given(expectedApp.getToken()).willReturn(expectedToken);
            // Act
            AppCommonResponse actualResult = appService.getAppByToken(expectedTokenString);
            // Assert
            assertThat(actualResult)
                .extracting("token")
                .isEqualTo(expectedTokenString);
        }

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
        @DisplayName("ID 조회시 앱을 찾을 수 없으면 예외를 던진다.")
        void willThrowExceptionWhenAppNotFoundById() {
            // Arrange
            given(appRepository.findById(expectedAppId)).willReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.getAppById(expectedAppId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("앱을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("토큰 조회시 앱을 찾을 수 없으면 예외를 던진다.")
        void willThrowExceptionWhenAppNotFoundByToken() {
            // Arrange
            String notExistToken = UUID.randomUUID().toString();
            // Arrange
            given(appRepository.findByToken(any(UUID.class))).willReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> appService.getAppByToken(notExistToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("앱을 찾을 수 없습니다.");
        }
    }

}