package info.logbat_view.domain.log.repository;

import info.logbat_view.domain.log.domain.LogData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LogDataRepository extends ReactiveCrudRepository<LogData, Long> {

    Flux<LogData> findByAppKeyAndLogIdGreaterThanOrderByLogId(@NonNull byte[] appKey,
        @NonNull Long id);

}
