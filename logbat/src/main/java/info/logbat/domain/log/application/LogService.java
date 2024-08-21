package info.logbat.domain.log.application;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import info.logbat.domain.log.repository.LogRepository;
import info.logbat.domain.project.application.AppService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final AppService appService;

    @Deprecated(forRemoval = true)
    public long saveLog(CreateLogServiceRequest request) {
        String level = request.level();
        String data = request.data();
        LocalDateTime timestamp = request.timestamp();

        Long appId = appService.getAppIdByToken(request.appKey());

        Log log = Log.of(appId, level, data, timestamp);

        return logRepository.save(log);
    }

    public void saveLogs(String appKey, List<CreateLogRequest> requests) {
        Long appId = appService.getAppIdByToken(appKey);
        logRepository.saveAll(requests.stream()
            .map(request -> request.toEntity(appId))
            .toList());
    }

}
