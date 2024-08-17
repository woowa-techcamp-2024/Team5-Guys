package info.logbat_view.common.payload;

import info.logbat_view.domain.log.presentation.payload.response.LogCommonResponse;
import java.util.List;
import lombok.Getter;

@Getter
public class CursorPaginationResult {

    private final List<LogCommonResponse> data;
    private final Integer size;
    private final Long nextCursor;
    private final Boolean hasNext;

    private CursorPaginationResult(List<LogCommonResponse> data, Integer size, Long nextCursor,
        Boolean hasNext) {
        this.data = data;
        this.size = size;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    public static CursorPaginationResult of(List<LogCommonResponse> data, int size) {
        boolean hasNext = data.size() > size;
        List<LogCommonResponse> subList = hasNext ? data.subList(0, size) : data;
        Long nextCursor = subList.get(subList.size() - 1).id();
        return new CursorPaginationResult(subList, subList.size(), nextCursor, hasNext);
    }
}
