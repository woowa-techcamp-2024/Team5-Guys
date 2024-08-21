package info.logbat_meta.domain.project.repository;

import info.logbat_meta.domain.project.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppJpaRepository extends JpaRepository<App, Long> {

    Optional<App> findByAppKey(@NonNull UUID token);

    Optional<App> findByProject_IdAndId(@NonNull Long id, @NonNull Long id1);

    List<App> findByProject_Id(@NonNull Long id);
}
