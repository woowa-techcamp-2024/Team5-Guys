package info.logbat_view.domain.log.presentation.payload.response;

import info.logbat_view.domain.log.domain.Log;
import java.time.LocalDateTime;

public record LogCommonResponse(Long id, String level, String data, LocalDateTime timestamp) {

    public static LogCommonResponse from(Log log) {
        return new LogCommonResponse(
            log.getId(),
            log.getLevel().name(),
            log.getData(),
            log.getTimestamp()
        );
    }

}
