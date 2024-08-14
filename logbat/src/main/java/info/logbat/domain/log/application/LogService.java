package info.logbat.domain.log.application;

import info.logbat.domain.log.application.payload.request.CreateLogServiceRequest;
import info.logbat.domain.log.domain.Log;
import info.logbat.domain.log.repository.LogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

  private final LogRepository logRepository;

  public long saveLog(CreateLogServiceRequest request) {
    Long appId = request.applicationId();
    String level = request.level();
    String data = request.data();
    LocalDateTime timestamp = request.timestamp();
    // TODO Log 저장 전 Application ID 체크 로직 추가 필요

    Log log = Log.of(appId, level, data, timestamp);

    return logRepository.save(log);
  }
}
