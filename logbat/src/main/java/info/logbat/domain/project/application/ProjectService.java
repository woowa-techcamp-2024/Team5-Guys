package info.logbat.domain.project.application;

import info.logbat.domain.project.domain.Project;
import info.logbat.domain.project.presentation.payload.request.ProjectUpdateRequest;
import info.logbat.domain.project.presentation.payload.response.ProjectCommonResponse;
import info.logbat.domain.project.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectRepository;

    public ProjectCommonResponse createProject(String name) {
        Project project = projectRepository.save(Project.from(name));
        return ProjectCommonResponse.from(project);
    }


    public ProjectCommonResponse getProjectByName(String name) {
        Project project = projectRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return ProjectCommonResponse.from(project);
    }

    public ProjectCommonResponse updateProjectValues(ProjectUpdateRequest request) {
        Project project = projectRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        project.updateName(request.name());
        return ProjectCommonResponse.from(project);
    }
}
