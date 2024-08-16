package info.logbat_view.domain.log.presentation;

import info.logbat_view.common.payload.CursorPaginationResult;
import info.logbat_view.domain.log.application.LogViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/logs")
@RestController
@RequiredArgsConstructor
public class LogViewController {

    private final LogViewService logViewService;

    @GetMapping("/{appKey}")
    public Mono<CursorPaginationResult> getLogs(@PathVariable String appKey,
        @RequestParam(name = "cursor", defaultValue = "-1", required = false) Long cursor,
        @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("appKey: {}, cursor: {}, size: {}", appKey, cursor, size);
        return logViewService.findLogs(appKey, cursor, size + 1)
            .collectList()
            .map(data -> CursorPaginationResult.of(data, size));
    }

}
