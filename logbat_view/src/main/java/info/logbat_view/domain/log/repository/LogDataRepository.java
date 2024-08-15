package info.logbat_view.domain.log.repository;

import info.logbat_view.domain.log.domain.LogData;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LogDataRepository extends ReactiveCrudRepository<LogData, Long> {

    Flux<LogData> findByAppKeyAndLogIdGreaterThanOrderByLogId(@NonNull UUID appKey,
        @NonNull Long id);
}
