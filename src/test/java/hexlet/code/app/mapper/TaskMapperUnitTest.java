package hexlet.code.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.model.Task;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class TaskMapperUnitTest {

    private final TaskMapper taskMapper = new TaskMapperImpl();

    @Test
    void shouldUpdateOnlyPresentScalarFields() {
        var task = Instancio.of(Task.class).ignore(field(Task::getTaskStatus)).ignore(field(Task::getAssignee))
                .ignore(field(Task::getLabels)).set(field(Task::getName), "Old title")
                .set(field(Task::getDescription), "Old content").set(field(Task::getIndex), 10).create();
        var request = Instancio.createBlank(TaskUpdateRequest.class);
        request.setTitle("New title");
        request.setContent(null);

        taskMapper.update(request, task);

        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getIndex()).isEqualTo(10);
    }
}
