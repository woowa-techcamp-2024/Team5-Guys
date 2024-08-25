package info.logbat_meta.domain.project.presentation;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import info.logbat_meta.common.payload.ApiCommonResponse;
import info.logbat_meta.domain.project.application.AppService;
import info.logbat_meta.domain.project.presentation.payload.request.AppCreateRequest;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/v1/projects/{projectId}/apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping
    public ApiCommonResponse<List<AppCommonResponse>> getAppsByProjectId(
        @PathVariable Long projectId) {
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
        AppCommonResponse app = appService.createApp(projectId, appCreateRequest.name(),
            appCreateRequest.appType());

        return ApiCommonResponse.createSuccessResponse(app);
    }

    @DeleteMapping("/{appId}")
    public ApiCommonResponse<Void> deleteApp(
        @PathVariable Long projectId,
        @PathVariable Long appId
    ) {
        UUID deletedAppToken = appService.deleteApp(projectId, appId);
        sendAPIRequest(deletedAppToken.toString());
        return ApiCommonResponse.createSuccessResponse();
    }

    private void sendAPIRequest(String uuid) {
        RestClient restClient = RestClient.create("https://api.logbat.info/");
        restClient.delete().uri("/apps/" + uuid)
            .retrieve()
            .onStatus(status -> status != NO_CONTENT, (request1, response) -> {
                throw new IllegalArgumentException("API 요청 실패");
            })
            .toEntity(Void.class);
    }

}
