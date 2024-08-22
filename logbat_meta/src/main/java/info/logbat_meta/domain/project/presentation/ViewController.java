package info.logbat_meta.domain.project.presentation;

import info.logbat_meta.domain.project.application.AppService;
import info.logbat_meta.domain.project.application.ProjectService;
import info.logbat_meta.domain.project.presentation.payload.response.AppCommonResponse;
import info.logbat_meta.domain.project.presentation.payload.response.ProjectCommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        List<AppCommonResponse> apps = appService.getAppsByProjectId(project.id());
        model.addAttribute("apps", apps);

        return "project_detail";
    }
}