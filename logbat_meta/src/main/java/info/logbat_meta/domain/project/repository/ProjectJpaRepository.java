package info.logbat_meta.domain.project.repository;

import info.logbat_meta.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(@NonNull String name);
}
