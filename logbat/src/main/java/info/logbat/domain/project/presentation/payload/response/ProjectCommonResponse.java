package info.logbat.domain.project.presentation.payload.response;

import info.logbat.domain.project.domain.Project;
import java.time.LocalDateTime;

public record ProjectCommonResponse(Long id, String name, LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {

    public static ProjectCommonResponse from(Project project) {
        return new ProjectCommonResponse(project.getId(), project.getName(), project.getCreatedAt(),
            project.getUpdatedAt());
    }
}
