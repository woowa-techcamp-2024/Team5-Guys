package info.logbat.domain.log.application.payload.request;

import java.time.LocalDateTime;

public record CreateLogServiceRequest(
    Long applicationId,
    String logLevel,
    String logData,
    LocalDateTime timestamp
) {

}
