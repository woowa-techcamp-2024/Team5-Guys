package info.logbat.domain.project.presentation.payload.request;

import jakarta.validation.constraints.NotBlank;

public record AppCacheRemoveRequest(@NotBlank(message = "토큰은 Null일 수 없습니다.") String token) {

}
