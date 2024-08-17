package info.logbat_view.domain.log.presentation.payload.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LogCursorRequest(Long cursor, Integer size) {

    @JsonCreator
    public LogCursorRequest(@JsonProperty("cursor") Long cursor,
        @JsonProperty("size") Integer size) {
        this.cursor = cursor == null ? -1 : cursor;
        this.size = size == null ? 10 : size;
    }
}
