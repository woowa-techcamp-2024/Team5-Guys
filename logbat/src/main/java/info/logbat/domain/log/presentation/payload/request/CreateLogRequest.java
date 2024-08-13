package info.logbat.domain.log.presentation.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CreateLogRequest {

  @NotBlank
  private final String logLevel;

  @NotBlank
  private final String logData;

  @NotNull
  private final LocalDateTime timestamp;

  public CreateLogRequest(String logLevel, String logData,
      LocalDateTime timestamp) {
    this.logLevel = logLevel;
    this.logData = logData;
    this.timestamp = timestamp;
  }
}
