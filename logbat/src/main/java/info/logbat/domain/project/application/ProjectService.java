package info.logbat.domain.project.application;

import info.logbat.domain.project.domain.Project;
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
}
