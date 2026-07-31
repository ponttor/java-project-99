package hexlet.code.app.repository;

import java.util.List;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT DISTINCT task
            FROM Task task
            LEFT JOIN FETCH task.labels
            WHERE (:titleCont IS NULL
                    OR LOWER(task.name) LIKE LOWER(CONCAT('%', :titleCont, '%')))
              AND (:assigneeId IS NULL OR task.assignee.id = :assigneeId)
              AND (:status IS NULL OR task.taskStatus.slug = :status)
              AND (:labelId IS NULL
                    OR EXISTS (
                        SELECT linkedTask.id
                        FROM Task linkedTask
                        JOIN linkedTask.labels label
                        WHERE linkedTask = task
                          AND label.id = :labelId
                    ))
            """)
    List<Task> findAllByFilters(
            @Param("titleCont") String titleCont,
            @Param("assigneeId") Long assigneeId,
            @Param("status") String status,
            @Param("labelId") Long labelId
    );
}
