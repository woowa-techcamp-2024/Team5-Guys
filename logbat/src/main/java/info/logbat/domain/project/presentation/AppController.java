package info.logbat.domain.project.presentation;

import info.logbat.common.payload.ApiCommonResponse;
import info.logbat.domain.project.application.AppService;
import info.logbat.domain.project.presentation.payload.response.AppCommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
