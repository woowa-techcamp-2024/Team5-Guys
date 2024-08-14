package info.logbat.domain.log.application.payload.request;

import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import java.time.LocalDateTime;

public record CreateLogServiceRequest(
    String appKey,
    String level,
    String data,
    LocalDateTime timestamp
) {

  public static CreateLogServiceRequest of(String appKey, CreateLogRequest request) {
    return new CreateLogServiceRequest(
        appKey,
        request.level(),
        request.data(),
        request.timestamp()
    );
  }
}
