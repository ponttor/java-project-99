package hexlet.code.app.repository;

import java.util.Optional;

import hexlet.code.app.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    boolean existsByName(String name);

    Optional<TaskStatus> findBySlug(String slug);
}
