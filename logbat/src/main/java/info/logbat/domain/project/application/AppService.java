package info.logbat.domain.project.application;

import info.logbat.domain.project.domain.App;
import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.domain.enums.AppType;
import info.logbat.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat.domain.project.repository.AppJpaRepository;
import info.logbat.domain.project.repository.ProjectJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppService {

    private final AppJpaRepository appRepository;
    private final ProjectJpaRepository projectRepository;

    public AppCommonResponse createApp(Long projectId, AppType appType) {
        Project project = getProject(projectId);
        return AppCommonResponse.from(appRepository.save(App.of(project, appType)));
    }

    public AppCommonResponse getAppByToken(String token) {
        UUID tokenUUID = UUID.fromString(token);
        App app = appRepository.findByToken(tokenUUID)
            .orElseThrow(() -> new IllegalArgumentException("앱을 찾을 수 없습니다."));
        return AppCommonResponse.from(app);
    }

    public AppCommonResponse getAppById(Long id) {
        App app = appRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("앱을 찾을 수 없습니다."));
        return AppCommonResponse.from(app);
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }
}
