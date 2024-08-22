package info.logbat_meta.domain.project.presentation.payload.response;

import info.logbat_meta.domain.project.domain.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProjectCommonResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static ProjectCommonResponse from(Project project) {
        return new ProjectCommonResponse(project.getId(), project.getName(), project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
