package hexlet.code.app.service;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskFilterParams;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserRepository userRepository;

    private final LabelRepository labelRepository;

    private final TaskMapper taskMapper;

    private final TaskSpecification taskSpecification;

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(TaskFilterParams params) {
        return taskMapper.toResponses(taskRepository.findAll(taskSpecification.build(params)));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskMapper.toResponse(findTask(id));
    }

    public TaskResponse create(TaskCreateRequest request) {
        var task = taskMapper.toEntity(request);
        var taskStatus = findTaskStatus(request.getStatus());
        task.setTaskStatus(taskStatus);
        task.setLabels(findLabels(request.getTaskLabelIds()));

        if (task.getIndex() == null) {
            var nextIndex = taskRepository.findMaxIndexByTaskStatus(taskStatus).map(index -> index + 1).orElse(0);
            task.setIndex(nextIndex);
        }

        if (request.getAssigneeId() != null) {
            task.setAssignee(findAssignee(request.getAssigneeId()));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(Long id, TaskUpdateRequest request) {
        var task = findTask(id);

        taskMapper.update(request, task);

        if (request.getAssigneeId().isPresent()) {
            var assigneeId = request.getAssigneeId().orElse(null);
            task.setAssignee(assigneeId == null ? null : findAssignee(assigneeId));
        }

        if (request.getStatus().isPresent()) {
            task.setTaskStatus(findTaskStatus(request.getStatus().orElse(null)));
        }

        if (request.getTaskLabelIds().isPresent()) {
            task.setLabels(findLabels(request.getTaskLabelIds().orElse(null)));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.delete(findTask(id));
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private TaskStatus findTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
    }

    private User findAssignee(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
    }

    private LinkedHashSet<Label> findLabels(List<Long> ids) {
        var labelsById = labelRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Label::getId, Function.identity()));

        return ids.stream().map(id -> {
            var label = labelsById.get(id);
            if (label == null) {
                throw new ResourceNotFoundException("Label not found");
            }
            return label;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
