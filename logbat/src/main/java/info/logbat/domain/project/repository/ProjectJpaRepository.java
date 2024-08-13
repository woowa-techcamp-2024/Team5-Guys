package info.logbat.domain.project.repository;

import info.logbat.domain.project.domain.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(@NonNull String name);
}
