package hexlet.code.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.model.Task;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class TaskMapperUnitTest {

    private final JsonNullableMapper jsonNullableMapper = new JsonNullableMapperImpl();

    private final TaskMapper taskMapper = new TaskMapperImpl(jsonNullableMapper);

    @Test
    void shouldUpdateOnlyPresentScalarFields() {
        var task = Instancio.of(Task.class).ignore(field(Task::getTaskStatus)).ignore(field(Task::getAssignee))
                .ignore(field(Task::getLabels)).set(field(Task::getName), "Old title")
                .set(field(Task::getDescription), "Old content").set(field(Task::getIndex), 10).create();
        var request = Instancio.createBlank(TaskUpdateRequest.class);
        request.setTitle(JsonNullable.of("New title"));
        request.setContent(JsonNullable.of(null));

        taskMapper.update(request, task);

        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getIndex()).isEqualTo(10);
    }

    @Test
    void shouldReplaceAllScalarFields() {
        var task = Instancio.of(Task.class).ignore(field(Task::getTaskStatus)).ignore(field(Task::getAssignee))
                .ignore(field(Task::getLabels)).set(field(Task::getName), "Old title")
                .set(field(Task::getDescription), "Old content").set(field(Task::getIndex), 10).create();
        var request = Instancio.createBlank(TaskCreateRequest.class);
        request.setTitle("New title");

        taskMapper.replace(request, task);

        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getIndex()).isNull();
    }

    @Test
    void shouldIgnoreNullJsonNullableWrapper() {
        assertThat(jsonNullableMapper.isPresent(null)).isFalse();
        assertThat(jsonNullableMapper.<String>unwrap(null)).isNull();
        assertThat(jsonNullableMapper.wrap("title")).isEqualTo(JsonNullable.of("title"));
        assertThat(taskMapper.toEntity(null)).isNull();
    }
}
