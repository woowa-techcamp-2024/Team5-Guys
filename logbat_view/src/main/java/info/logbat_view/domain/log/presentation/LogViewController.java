package info.logbat_view.domain.log.presentation;

import info.logbat_view.common.payload.CursorPaginationResult;
import info.logbat_view.domain.log.application.LogViewService;
import info.logbat_view.domain.log.presentation.payload.request.LogCursorRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/logs")
@RestController
@RequiredArgsConstructor
public class LogViewController {

    private final LogViewService logViewService;

    @GetMapping("/{appKey}")
    public Mono<CursorPaginationResult> getLogs(@PathVariable String appKey, @RequestBody
    LogCursorRequest request) {
        log.info("appKey: {}, cursor: {}, size: {}", appKey, request.cursor(), request.size());
        return logViewService.findLogs(appKey, request.cursor(), request.size() + 1)
            .collectList()
            .map(data -> CursorPaginationResult.of(data, request.size()));
    }

}
