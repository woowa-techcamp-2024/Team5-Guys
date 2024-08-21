package info.logbat.domain.log.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import info.logbat.dev.aop.CountTest;
import info.logbat.domain.log.application.LogService;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @CountTest
    @PostMapping
    public ResponseEntity<Void> saveLogs(
        @RequestHeader("App-Key") @NotBlank(message = "appKey가 비어있습니다.") String appKey,
        @Valid @RequestBody List<CreateLogRequest> request) {
        logService.saveLogs(appKey, request);
        return ResponseEntity.status(CREATED).build();
    }

}
