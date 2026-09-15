package hexlet.code.app.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class DomainModelEqualityTest {

    @Test
    void shouldCompareLabelsByIdInHashSet() {
        var label = label(1L, "feature");
        var sameLabel = label(1L, "renamed");
        var differentLabel = label(2L, "feature");

        var labels = new HashSet<>(List.of(label, sameLabel, differentLabel));

        assertThat(labels).containsExactlyInAnyOrder(label, differentLabel);
    }

    @Test
    void shouldCompareOtherDomainModelsById() {
        assertThat(task(1L)).isEqualTo(task(1L)).isNotEqualTo(task(2L));
        assertThat(taskStatus(1L)).isEqualTo(taskStatus(1L)).isNotEqualTo(taskStatus(2L));
        assertThat(user(1L)).isEqualTo(user(1L)).isNotEqualTo(user(2L));
    }

    @Test
    void shouldNotCompareTransientEntitiesAsEqual() {
        assertThat(new Label()).isNotEqualTo(new Label());
        assertThat(new Task()).isNotEqualTo(new Task());
        assertThat(new TaskStatus()).isNotEqualTo(new TaskStatus());
        assertThat(new User()).isNotEqualTo(new User());
    }

    @Test
    void shouldCompareOnlyEntitiesOfTheSameType() {
        var label = label(1L, "feature");

        assertThat(label).isEqualTo(label).isNotEqualTo(null).isNotEqualTo(task(1L));
    }

    @Test
    void shouldKeepHashCodeAfterGeneratedIdIsAssigned() {
        var label = new Label();
        var task = new Task();
        var taskStatus = new TaskStatus();
        var user = new User();

        assertStableHashCode(label, () -> label.setId(1L));
        assertStableHashCode(task, () -> task.setId(1L));
        assertStableHashCode(taskStatus, () -> taskStatus.setId(1L));
        assertStableHashCode(user, () -> user.setId(1L));
    }

    private void assertStableHashCode(Object entity, Runnable assignId) {
        var transientHashCode = entity.hashCode();

        assignId.run();

        assertThat(entity.hashCode()).isEqualTo(transientHashCode);
    }

    private Label label(Long id, String name) {
        var label = new Label();
        label.setId(id);
        label.setName(name);
        return label;
    }

    private Task task(Long id) {
        var task = new Task();
        task.setId(id);
        return task;
    }

    private TaskStatus taskStatus(Long id) {
        var taskStatus = new TaskStatus();
        taskStatus.setId(id);
        return taskStatus;
    }

    private User user(Long id) {
        var user = new User();
        user.setId(id);
        return user;
    }
}
