package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.common.payload.ApiCommonResponse;
import info.logbat_meta.domain.project.application.AppService;
import info.logbat_meta.domain.project.presentation.payload.request.AppCreateRequest;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping("/{projectId}")
    public ApiCommonResponse<List<AppCommonResponse>> getAppsByProjectId(
        @PathVariable Long projectId) {
        return ApiCommonResponse.createSuccessResponse(appService.getAppsByProjectId(projectId));
    }

    @GetMapping("/info/{id}")
    public ApiCommonResponse<AppCommonResponse> getAppById(@PathVariable Long id) {
        return ApiCommonResponse.createSuccessResponse(appService.getAppById(id));
    }

    @PostMapping
    public ApiCommonResponse<AppCommonResponse> createApp(
        @RequestBody AppCreateRequest appCreateRequest) {
        return ApiCommonResponse.createSuccessResponse(
            appService.createApp(appCreateRequest.projectId(), appCreateRequest.appType()));
    }

    @DeleteMapping("/{projectId}/{appId}")
    public ApiCommonResponse<Long> deleteApp(@PathVariable Long projectId,
        @PathVariable Long appId) {
        return ApiCommonResponse.createSuccessResponse(appService.deleteApp(projectId, appId));
    }

}
