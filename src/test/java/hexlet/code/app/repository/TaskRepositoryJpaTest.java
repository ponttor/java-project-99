package hexlet.code.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.config.JpaConfig;
import hexlet.code.app.config.PasswordConfig;
import hexlet.code.app.dto.task.TaskFilterParams;
import hexlet.code.app.model.Task;
import hexlet.code.app.specification.TaskSpecification;
import hexlet.code.app.util.ModelGenerator;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaConfig.class, PasswordConfig.class, TaskSpecification.class, ModelGenerator.class})
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

    @Autowired
    private ModelGenerator modelGenerator;

    @Test
    void shouldCombineAllTaskSpecifications() {
        var draft = taskStatusRepository.save(modelGenerator.taskStatus("Draft", "draft"));
        var published = taskStatusRepository.save(modelGenerator.taskStatus("Published", "published"));
        var assignee = userRepository.save(modelGenerator.user("worker@example.com"));
        var anotherAssignee = userRepository.save(modelGenerator.user("other@example.com"));
        var bug = labelRepository.save(modelGenerator.label("bug"));
        var feature = labelRepository.save(modelGenerator.label("feature"));

        var expected = modelGenerator.taskWithLabels("Create RELEASE", published, assignee, bug);
        taskRepository.save(expected);
        taskRepository.save(modelGenerator.taskWithLabels("Create draft", draft, assignee, bug));
        taskRepository.save(modelGenerator.taskWithLabels("Publish release", published, anotherAssignee, bug));
        taskRepository.save(modelGenerator.taskWithLabels("Create feature", published, assignee, feature));

        var params = Instancio.createBlank(TaskFilterParams.class);
        params.setTitleCont("release");
        params.setAssigneeId(assignee.getId());
        params.setStatus("published");
        params.setLabelId(bug.getId());

        var result = taskRepository.findAll(taskSpecification.build(params));

        assertThat(result).extracting(Task::getId).containsExactly(expected.getId());
    }

    @Test
    void shouldReturnAllTasksWhenFiltersAreAbsent() {
        var status = taskStatusRepository.save(modelGenerator.taskStatus("Draft", "draft"));
        var first = taskRepository.save(modelGenerator.task("First", status));
        var second = taskRepository.save(modelGenerator.task("Second", status));

        var result = taskRepository.findAll(taskSpecification.build(Instancio.createBlank(TaskFilterParams.class)));

        assertThat(result).extracting(Task::getId).containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void shouldLoadTaskRelationsWithRepositoryEntityGraph() {
        var status = taskStatusRepository.save(modelGenerator.taskStatus("Draft", "draft"));
        var assignee = userRepository.save(modelGenerator.user("worker@example.com"));
        var bug = labelRepository.save(modelGenerator.label("bug"));
        var task = taskRepository.save(modelGenerator.taskWithLabels("Task", status, assignee, bug));
        entityManager.flush();
        entityManager.clear();

        var result = taskRepository.findAll(taskSpecification.build(Instancio.createBlank(TaskFilterParams.class)));

        assertThat(result).hasSize(1);
        var loadedTask = result.getFirst();
        assertThat(loadedTask.getId()).isEqualTo(task.getId());
        assertThat(Hibernate.isInitialized(loadedTask.getTaskStatus())).isTrue();
        assertThat(Hibernate.isInitialized(loadedTask.getAssignee())).isTrue();
        assertThat(Hibernate.isInitialized(loadedTask.getLabels())).isTrue();
    }

}
