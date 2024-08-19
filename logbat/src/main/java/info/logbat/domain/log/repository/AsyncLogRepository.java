package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.util.Optional;

public class AsyncLogRepository implements LogRepository {

  @Override
  public long save(Log log) {
    return 0;
  }

  @Override
  public Optional<Log> findById(Long logId) {
    return Optional.empty();
  }
}
