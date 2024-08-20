package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.common.payload.ApiCommonResponse;
import info.logbat_meta.domain.project.application.ProjectService;
import info.logbat_meta.domain.project.presentation.payload.request.ProjectCreateRequest;
import info.logbat_meta.domain.project.presentation.payload.request.ProjectUpdateRequest;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ApiCommonResponse<ProjectCommonResponse> get(@PathVariable Long projectId) {
        ProjectCommonResponse project = projectService.getProjectById(projectId);

        return ApiCommonResponse.createSuccessResponse(project);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiCommonResponse<ProjectCommonResponse> create(@RequestBody ProjectCreateRequest request) {
        ProjectCommonResponse project = projectService.createProject(request.name());

        return ApiCommonResponse.createApiResponse(HttpStatus.CREATED, "Success", project);
    }

    @PutMapping("/{projectId}")
    public ApiCommonResponse<ProjectCommonResponse> update(
            @PathVariable Long projectId,
            @RequestBody ProjectUpdateRequest request
    ) {
        ProjectCommonResponse data = projectService.updateProjectValues(projectId, request.name());

        return ApiCommonResponse.createSuccessResponse(data);
    }

    @DeleteMapping("/{projectId}")
    public ApiCommonResponse<Long> delete(@PathVariable Long projectId) {
        Long deletedId = projectService.deleteProject(projectId);
        return ApiCommonResponse.createSuccessResponse(deletedId);
    }

}
