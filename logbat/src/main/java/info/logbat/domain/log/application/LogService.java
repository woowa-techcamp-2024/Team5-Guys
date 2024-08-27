package info.logbat.domain.log.application;

import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import info.logbat.domain.log.repository.LogRepository;
import info.logbat.domain.project.application.AppService;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final AppService appService;

    public void saveLogs(String appKey, List<CreateLogRequest> requests) {
        Long appId = appService.getAppIdByToken(appKey);
        List<Log> logs = new ArrayList<>(requests.size());
        requests.forEach(request -> {
            try {
                logs.add(request.toEntity(appId));
            }
            catch (Exception e) {
                log.error("Failed to convert request to entity: {}", request, e);
            }
        });
        logRepository.saveAll(logs);
    }

}
