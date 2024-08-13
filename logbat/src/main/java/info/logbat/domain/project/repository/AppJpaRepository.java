package info.logbat.domain.project.repository;

import info.logbat.domain.project.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppJpaRepository extends JpaRepository<App, Long> {

}
