package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.common.payload.ApiCommonResponse;
import info.logbat_meta.domain.project.application.AppService;
import info.logbat_meta.domain.project.presentation.payload.request.AppCreateRequest;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping
    public ApiCommonResponse<List<AppCommonResponse>> getAppsByProjectId(@PathVariable Long projectId) {
        List<AppCommonResponse> apps = appService.getAppsByProjectId(projectId);

        return ApiCommonResponse.createSuccessResponse(apps);
    }

    @GetMapping("/{appId}")
    public ApiCommonResponse<AppCommonResponse> getAppById(@PathVariable Long appId) {
        AppCommonResponse app = appService.getAppById(appId);

        return ApiCommonResponse.createSuccessResponse(app);
    }

    @PostMapping
    public ApiCommonResponse<AppCommonResponse> createApp(
            @PathVariable Long projectId,
            @RequestBody AppCreateRequest appCreateRequest) {
        AppCommonResponse app = appService.createApp(projectId, appCreateRequest.name(), appCreateRequest.appType());

        return ApiCommonResponse.createSuccessResponse(app);
    }

    @DeleteMapping("/{appId}")
    public ApiCommonResponse<Long> deleteApp(
            @PathVariable Long projectId,
            @PathVariable Long appId
    ) {
        Long deletedId = appService.deleteApp(projectId, appId);

        return ApiCommonResponse.createSuccessResponse(deletedId);
    }

}
