package info.logbat_meta.domain.project.presentation.payload.response;

import info.logbat_meta.domain.project.domain.App;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AppCommonResponse {
    private final Long id;
    private final Long projectId;
    private final String name;
    private final String appType;
    private final String token;
    private final LocalDateTime createdAt;

    public static AppCommonResponse from(App app) {
        return new AppCommonResponse(app.getId(), app.getProject().getId(), app.getName(), app.getAppType().name(),
                app.getAppKey().toString(), app.getCreatedAt());
    }
}

