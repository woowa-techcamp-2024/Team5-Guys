package info.logbat.domain.log.application;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.repository.LogRepository;
import info.logbat.domain.project.repository.AppJpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final AppJpaRepository appJpaRepository;

    public long saveLog(CreateLogServiceRequest request) {
        String level = request.level();
        String data = request.data();
        LocalDateTime timestamp = request.timestamp();

        Long appId = getAppId(request.appKey());

        Log log = Log.of(appId, level, data, timestamp);

        return logRepository.save(log);
    }

    private Long getAppId(String appKey) {
        UUID appKeyUuid;

        try {
            appKeyUuid = UUID.fromString(appKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 형식의 Application Key 입니다.");
        }
        return appJpaRepository.findByAppKey(appKeyUuid)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Application Key 입니다."))
            .getId();
    }
}
