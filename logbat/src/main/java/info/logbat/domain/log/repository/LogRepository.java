package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.util.Optional;

public interface LogRepository {

  long save(Log log);

  Optional<Log> findById(Long logId);
}
