package info.logbat_meta.domain.project.presentation.payload.response;

import info.logbat_meta.domain.project.domain.App;

import java.time.LocalDateTime;

public record AppCommonResponse(Long id, Long projectId, String appType, String token,
                                LocalDateTime createdAt) {

    public static AppCommonResponse from(App app) {
        return new AppCommonResponse(app.getId(), app.getProject().getId(), app.getAppType().name(),
            app.getAppKey().toString(), app.getCreatedAt());
    }
}
