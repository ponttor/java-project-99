package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.config.JpaConfig;
import hexlet.code.app.dto.task.TaskFilterParams;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.persistence.EntityManager;
import java.util.LinkedHashSet;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaConfig.class, TaskSpecification.class})
class TaskRepositoryJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskSpecification taskSpecification;

    @Test
    void shouldCombineAllTaskSpecifications() {
        var draft = taskStatusRepository.save(taskStatus("Draft", "draft"));
        var published = taskStatusRepository.save(taskStatus("Published", "published"));
        var assignee = userRepository.save(user("worker@example.com"));
        var anotherAssignee = userRepository.save(user("other@example.com"));
        var bug = labelRepository.save(label("bug"));
        var feature = labelRepository.save(label("feature"));

        var expected = task("Create RELEASE", published, assignee, bug);
        taskRepository.save(expected);
        taskRepository.save(task("Create draft", draft, assignee, bug));
        taskRepository.save(task("Publish release", published, anotherAssignee, bug));
        taskRepository.save(task("Create feature", published, assignee, feature));

        var params = new TaskFilterParams();
        params.setTitleCont("release");
        params.setAssigneeId(assignee.getId());
        params.setStatus("published");
        params.setLabelId(bug.getId());

        var result = taskRepository.findAll(taskSpecification.build(params));

        assertThat(result).extracting(Task::getId).containsExactly(expected.getId());
    }

    @Test
    void shouldReturnAllTasksWhenFiltersAreAbsent() {
        var status = taskStatusRepository.save(taskStatus("Draft", "draft"));
        var first = taskRepository.save(task("First", status));
        var second = taskRepository.save(task("Second", status));

        var result = taskRepository.findAll(taskSpecification.build(new TaskFilterParams()));

        assertThat(result).extracting(Task::getId).containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void shouldLoadTaskRelationsWithRepositoryEntityGraph() {
        var status = taskStatusRepository.save(taskStatus("Draft", "draft"));
        var assignee = userRepository.save(user("worker@example.com"));
        var bug = labelRepository.save(label("bug"));
        var task = taskRepository.save(task("Task", status, assignee, bug));
        entityManager.flush();
        entityManager.clear();

        var result = taskRepository.findAll(taskSpecification.build(new TaskFilterParams()));

        assertThat(result).hasSize(1);
        var loadedTask = result.getFirst();
        assertThat(loadedTask.getId()).isEqualTo(task.getId());
        assertThat(Hibernate.isInitialized(loadedTask.getTaskStatus())).isTrue();
        assertThat(Hibernate.isInitialized(loadedTask.getAssignee())).isTrue();
        assertThat(Hibernate.isInitialized(loadedTask.getLabels())).isTrue();
    }

    private Task task(String name, TaskStatus status) {
        return task(name, status, null);
    }

    private Task task(String name, TaskStatus status, User assignee, Label... labels) {
        var task = new Task();
        task.setName(name);
        task.setTaskStatus(status);
        task.setAssignee(assignee);
        task.setLabels(new LinkedHashSet<>(java.util.List.of(labels)));
        return task;
    }

    private TaskStatus taskStatus(String name, String slug) {
        var status = new TaskStatus();
        status.setName(name);
        status.setSlug(slug);
        return status;
    }

    private User user(String email) {
        var user = new User();
        user.setEmail(email);
        user.setPassword("password");
        return user;
    }

    private Label label(String name) {
        var label = new Label();
        label.setName(name);
        return label;
    }
}
