package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.common.payload.ApiCommonResponse;
import info.logbat_meta.domain.project.application.ProjectService;
import info.logbat_meta.domain.project.presentation.payload.request.ProjectCreateRequest;
import info.logbat_meta.domain.project.presentation.payload.request.ProjectUpdateRequest;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{name}")
    public ApiCommonResponse<ProjectCommonResponse> get(@PathVariable String name) {
        return ApiCommonResponse.createSuccessResponse(projectService.getProjectByName(name));
    }

    @PostMapping
    public ResponseEntity<ApiCommonResponse<ProjectCommonResponse>> create(
        @RequestBody ProjectCreateRequest request) {
        ApiCommonResponse<ProjectCommonResponse> response = ApiCommonResponse.createApiResponse(
            HttpStatus.CREATED, "Success", projectService.createProject(request.name()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ApiCommonResponse<ProjectCommonResponse> update(@PathVariable Long id,
        @RequestBody ProjectUpdateRequest request) {
        return ApiCommonResponse.createSuccessResponse(
            projectService.updateProjectValues(id, request.name()));
    }

    @DeleteMapping("/{id}")
    public ApiCommonResponse<Long> delete(@PathVariable Long id) {
        Long expectedId = projectService.deleteProject(id);
        return ApiCommonResponse.createSuccessResponse(expectedId);
    }

}
