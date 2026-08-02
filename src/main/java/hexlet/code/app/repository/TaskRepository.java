package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Override
    @EntityGraph(attributePaths = {"labels", "assignee", "taskStatus"})
    List<Task> findAll(Specification<Task> specification);

    @Query("select max(task.index) from Task task where task.taskStatus = :taskStatus")
    Optional<Integer> findMaxIndexByTaskStatus(TaskStatus taskStatus);
}
