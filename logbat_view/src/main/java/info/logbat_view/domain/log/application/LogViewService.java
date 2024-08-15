package info.logbat_view.domain.log.application;

import info.logbat_view.common.util.UUIDConvertor;
import info.logbat_view.domain.log.domain.service.LogService;
import info.logbat_view.domain.log.presentation.payload.response.LogCommonResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LogViewService {

    private final LogService logService;

    public Flux<LogCommonResponse> findLogs(String appKey, Long id, Integer size) {
        UUID appKeyUUID = UUIDConvertor.convertStringToUUID(appKey);
        return logService.findLogsByAppKey(appKeyUUID, id, size).map(LogCommonResponse::from);
    }


}
