package info.logbat.domain.log.repository;

import info.logbat.domain.log.domain.Log;
import java.util.List;
import java.util.Optional;

public interface LogRepository {

    long save(Log log);

    List<Log> saveAll(List<Log> logs);

    Optional<Log> findById(Long logId);
}
