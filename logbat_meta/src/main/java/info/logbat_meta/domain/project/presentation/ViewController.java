package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.domain.project.application.AppService;
import info.logbat_meta.domain.project.application.ProjectService;
import info.logbat_meta.domain.project.domain.enums.AppType;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ViewController {

    private final ProjectService projectService;
    private final AppService appService;

    @GetMapping
    public String projects() {
        return "projects";
    }

    @GetMapping("/search")
    public String searchProjectByName(@RequestParam String name, Model model) {
        // 프로젝트가 없으면, IllegalArgumentException 발생
        ProjectCommonResponse project;

        try {
            project = projectService.getProjectByName(name);
        } catch (IllegalArgumentException e) {
            return "project_not_found";
        }

        model.addAttribute("project", project);

        List<AppCommonResponse> apps = appService.getAppsByProjectId(project.getId());
        model.addAttribute("apps", apps);

        return "project_detail";
    }

    @GetMapping("/create")
    public String createProject() {
        return "create_project";
    }

    @PostMapping("/create")
    public String createProject(@RequestParam String name) {
        projectService.createProject(name);
        return "redirect:/projects/search?name=" + name;
    }

    @PostMapping("/apps/create")
    public String createApp(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String appType,
            Model model
    ) {
        if (projectId == null) {
            return "redirect:/";
        }

        model.addAttribute("appTypes", AppType.values());  // Enum 값들을 모델에 추가

        ProjectCommonResponse project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        if (name.isBlank() || appType.isBlank()) {
            return "create_app";
        }
        appService.createApp(projectId, name, appType);
        return "redirect:/projects/search?name=" + projectService.getProjectById(projectId).getName();
    }

    @GetMapping("/{projectId}/apps/{appId}/delete")
    public String deleteApp(
            @PathVariable Long projectId,
            @PathVariable Long appId
    ) {
        ProjectCommonResponse project = projectService.getProjectById(projectId);
        appService.deleteApp(project.getId(), appId);
        return "redirect:/projects/search?name=" + project.getName();
    }
}