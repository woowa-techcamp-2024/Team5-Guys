package info.logbat.infrastructure.payload;

import java.time.LocalDateTime;

public record LogSendRequest(
    String level,
    String data,
    LocalDateTime timestamp
) {

}
