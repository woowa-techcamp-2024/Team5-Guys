package info.logbat.domain.log.presentation;

import info.logbat.domain.log.application.LogService;
import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogController {

  private final LogService logService;

  @PostMapping("/logs")
  public ResponseEntity<Void> saveLog(
      @RequestHeader("app_id") @NotNull @Positive Long applicationId,
      @Valid @RequestBody CreateLogRequest request
  ) {

    logService.saveLog(CreateLogServiceRequest.of(applicationId, request));

    return ResponseEntity.ok()
        .build();
  }

}
