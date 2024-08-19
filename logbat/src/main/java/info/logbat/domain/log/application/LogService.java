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
    String appKey = request.appKey();
    String level = request.level();
    String data = request.data();
    LocalDateTime timestamp = request.timestamp();

    UUID appKeyUuid = getAppKey(appKey);

    if (!appJpaRepository.existsByToken(appKeyUuid)) {
      throw new IllegalArgumentException("존재하지 않는 Application Key 입니다.");
    }

    Log log = Log.of(appKey, level, data, timestamp);

    return logRepository.save(log);
  }

  private UUID getAppKey(String appKey) {
    try {
      return UUID.fromString(appKey);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("잘못된 형식의 Application Key 입니다.");
    }
  }
}
