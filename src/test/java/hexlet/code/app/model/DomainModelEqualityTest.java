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
