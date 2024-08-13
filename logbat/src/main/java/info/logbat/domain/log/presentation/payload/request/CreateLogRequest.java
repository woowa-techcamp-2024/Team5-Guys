package info.logbat.domain.log.presentation.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateLogRequest(
    @NotBlank
    String logLevel,

    @NotBlank
    String logData,

    @NotNull
    LocalDateTime timestamp
) {

}
