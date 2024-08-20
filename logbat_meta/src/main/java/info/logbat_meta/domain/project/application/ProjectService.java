package info.logbat_meta.domain.project.application;

import info.logbat_meta.domain.project.domain.Project;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import info.logbat_meta.domain.project.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectJpaRepository projectRepository;

    public ProjectCommonResponse createProject(String name) {
        Project project = projectRepository.save(Project.from(name));
        return ProjectCommonResponse.from(project);
    }

    @Transactional(readOnly = true)
    public ProjectCommonResponse getProjectByName(String name) {
        Project project = projectRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        return ProjectCommonResponse.from(project);
    }

    @Transactional(readOnly = true)
    public ProjectCommonResponse getProjectById(Long id) {
        Project project = getProject(id);
        return ProjectCommonResponse.from(project);
    }

    public ProjectCommonResponse updateProjectValues(Long id, String name) {
        Project project = getProject(id);
        project.updateName(name);
        return ProjectCommonResponse.from(project);
    }

    public Long deleteProject(Long id) {
        Project project = getProject(id);
        projectRepository.delete(project);
        return id;
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
    }
}
