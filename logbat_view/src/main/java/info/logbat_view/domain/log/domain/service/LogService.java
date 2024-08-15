package info.logbat_view.domain.log.domain.service;

import info.logbat_view.domain.log.domain.Log;
import info.logbat_view.domain.log.repository.LogDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogDataRepository logDataRepository;

    public Flux<Log> findLogsByAppKey(String appKey, Long id, Integer size) {
        return logDataRepository.findByAppKeyAndLogIdGreaterThanOrderByLogId(appKey, id)
            .map(Log::from)
            .take(size);
    }

}
