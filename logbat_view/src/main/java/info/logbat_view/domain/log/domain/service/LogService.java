package info.logbat_view.domain.log.domain.service;

import info.logbat_view.domain.log.domain.Log;
import info.logbat_view.domain.log.repository.LogDataRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogDataRepository logDataRepository;

    public Flux<Log> findLogsByAppKey(UUID appKey, Long id, Integer size) {
        return logDataRepository.findByAppKeyAndLogIdGreaterThanOrderByLogId(appKey, id)
            .take(size)
            .map(Log::from);
    }

}