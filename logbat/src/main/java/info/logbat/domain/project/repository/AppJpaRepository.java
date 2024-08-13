package info.logbat.domain.project.repository;

import info.logbat.domain.project.domain.App;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface AppJpaRepository extends JpaRepository<App, Long> {

    Optional<App> findByToken(@NonNull UUID token);
}
