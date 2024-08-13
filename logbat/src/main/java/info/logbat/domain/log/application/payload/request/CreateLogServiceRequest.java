package info.logbat.domain.log.application.payload.request;

import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import java.time.LocalDateTime;

public record CreateLogServiceRequest(
    Long applicationId,
    String logLevel,
    String logData,
    LocalDateTime timestamp
) {

  public static CreateLogServiceRequest of(Long applicationId, CreateLogRequest request) {
    return new CreateLogServiceRequest(
        applicationId,
        request.getLogLevel(),
        request.getLogData(),
        request.getTimestamp()
    );
  }
}
