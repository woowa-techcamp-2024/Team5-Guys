package info.logbat.domain.project.application;

import info.logbat.domain.project.domain.App;
import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.domain.enums.AppType;
import info.logbat.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat.domain.project.repository.AppJpaRepository;
import info.logbat.domain.project.repository.ProjectJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AppService {

    private static final String APP_NOT_FOUND_MESSAGE = "앱을 찾을 수 없습니다.";

    private final AppJpaRepository appRepository;
    private final ProjectJpaRepository projectRepository;

    public AppCommonResponse createApp(Long projectId, String appTypeStr) {
        Project project = getProject(projectId);
        AppType appType = AppType.from(appTypeStr);
        return AppCommonResponse.from(appRepository.save(App.of(project, appType)));
    }

    @Transactional(readOnly = true)
    public AppCommonResponse getAppByToken(String token) {
        UUID tokenUUID = UUID.fromString(token);
        App app = appRepository.findByAppKey(tokenUUID)
            .orElseThrow(() -> new IllegalArgumentException(APP_NOT_FOUND_MESSAGE));
        return AppCommonResponse.from(app);
    }

    @Transactional(readOnly = true)
    public AppCommonResponse getAppById(Long id) {
        App app = appRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(APP_NOT_FOUND_MESSAGE));
        return AppCommonResponse.from(app);
    }

    @Transactional(readOnly = true)
    public List<AppCommonResponse> getAppsByProjectId(Long projectId) {
        List<App> apps = appRepository.findByProject_Id(projectId);
        return apps.stream().map(AppCommonResponse::from).toList();
    }

    public Long deleteApp(Long projectId, Long appId) {
        App app = appRepository.findByProject_IdAndId(projectId, appId)
            .orElseThrow(() -> new IllegalArgumentException(APP_NOT_FOUND_MESSAGE));
        appRepository.delete(app);
        return app.getId();
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }
}
