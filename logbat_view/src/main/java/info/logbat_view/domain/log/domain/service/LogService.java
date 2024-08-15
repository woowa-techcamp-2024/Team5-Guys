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

    public Flux<Log> findLogsByAppKey(String appKey) {
        return logDataRepository.findByAppKey(appKey).map(Log::from);
    }

}
