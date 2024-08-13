package info.logbat.domain.project.presentation.payload.response;

import info.logbat.domain.project.domain.Project;
import java.time.LocalDateTime;

public record ProjectCreateResponse(Long id, String name, LocalDateTime createdAt) {

    public static ProjectCreateResponse from(Project project) {
        return new ProjectCreateResponse(project.getId(), project.getName(),
            project.getCreatedAt());
    }
}
