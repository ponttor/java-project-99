package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Override
    @EntityGraph(attributePaths = {"labels", "assignee", "taskStatus"})
    List<Task> findAll(Specification<Task> specification);
}
