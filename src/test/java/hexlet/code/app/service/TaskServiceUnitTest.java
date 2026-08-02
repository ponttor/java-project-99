package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskSpecification taskSpecification;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldUpdateOnlyFieldsPresentInRequest() {
        var assignee = user(10L);
        var task = new Task();
        task.setName("Old title");
        task.setDescription("Keep content");
        task.setAssignee(assignee);
        task.setIndex(3);
        var request = new TaskUpdateRequest();
        request.setTitle("New title");
        request.setAssigneeId(null);
        var response = new TaskResponse();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        var result = taskService.update(1L, request);

        assertThat(result).isSameAs(response);
        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getDescription()).isEqualTo("Keep content");
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getIndex()).isEqualTo(3);
        verifyNoInteractions(taskStatusRepository, userRepository, labelRepository);
    }

    @Test
    void shouldResolveTaskRelationsWhenCreatingTask() {
        var request = new TaskCreateRequest();
        request.setStatus("published");
        request.setAssigneeId(9L);
        request.setTaskLabelIds(List.of(2L, 1L, 2L));
        var task = new Task();
        var status = taskStatus(5L, "published");
        var assignee = user(9L);
        var firstLabel = label(1L);
        var secondLabel = label(2L);
        var response = new TaskResponse();
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskStatusRepository.findBySlug("published")).thenReturn(Optional.of(status));
        when(userRepository.findById(9L)).thenReturn(Optional.of(assignee));
        when(labelRepository.findAllById(request.getTaskLabelIds())).thenReturn(List.of(firstLabel, secondLabel));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        var result = taskService.create(request);

        assertThat(result).isSameAs(response);
        assertThat(task.getTaskStatus()).isSameAs(status);
        assertThat(task.getAssignee()).isSameAs(assignee);
        assertThat(task.getLabels()).containsExactly(secondLabel, firstLabel);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldRejectMissingTaskStatusBeforeSavingTask() {
        var request = new TaskCreateRequest();
        request.setStatus("missing");
        var task = new Task();
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(taskStatusRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task status not found");

        verify(taskRepository, never()).save(task);
        verifyNoInteractions(userRepository, labelRepository);
    }

    private TaskStatus taskStatus(Long id, String slug) {
        var status = new TaskStatus();
        status.setId(id);
        status.setSlug(slug);
        return status;
    }

    private User user(Long id) {
        var user = new User();
        user.setId(id);
        return user;
    }

    private Label label(Long id) {
        var label = new Label();
        label.setId(id);
        return label;
    }
}
