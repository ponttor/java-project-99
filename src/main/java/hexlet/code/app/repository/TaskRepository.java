package hexlet.code.app.repository;

import java.util.List;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    @EntityGraph(attributePaths = {"labels"})
    List<Task> findAll();

    @EntityGraph(attributePaths = {"labels"})
    List<Task> findAllByLabelsId(Long labelId);
}
