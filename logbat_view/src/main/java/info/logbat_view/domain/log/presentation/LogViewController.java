package info.logbat_view.domain.log.presentation;

import info.logbat_view.domain.log.application.LogViewService;
import info.logbat_view.domain.log.presentation.payload.response.LogCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/logs")
@RestController
@RequiredArgsConstructor
public class LogViewController {

    private final LogViewService logViewService;

    @GetMapping("/{appKey}")
    public Flux<ResponseEntity<LogCommonResponse>> getLogs(@PathVariable String appKey,
        @RequestParam(defaultValue = "-1", required = false) Long cursor,
        @RequestParam(defaultValue = "10", required = false) Integer size) {
        return logViewService.findLogs(appKey, cursor, size).map(ResponseEntity::ok);
    }

}
