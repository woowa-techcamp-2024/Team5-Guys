package info.logbat.domain.log.presentation.payload.request;

import info.logbat.domain.log.domain.Log;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateLogRequest(@NotBlank(message = "로그 레벨이 비어있습니다.") String level,
                               @NotBlank(message = "로그 데이터가 비어있습니다.") String data,
                               @NotNull(message = "타임스탬프가 비어있습니다.") LocalDateTime timestamp) {

    public Log toEntity(Long appId) {
        return Log.of(appId, level, data, timestamp);
    }

}
