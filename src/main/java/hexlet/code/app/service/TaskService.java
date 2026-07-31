package hexlet.code.app.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import hexlet.code.app.dto.TaskCreateRequest;
import hexlet.code.app.dto.TaskFilterParams;
import hexlet.code.app.dto.TaskResponse;
import hexlet.code.app.dto.TaskUpdateRequest;
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
        return taskRepository.findAll(taskSpecification.build(params)).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskMapper.toResponse(findTask(id));
    }

    public TaskResponse create(TaskCreateRequest request) {
        var task = taskMapper.toEntity(request);
        task.setTaskStatus(findTaskStatus(request.getStatus()));
        task.setLabels(findLabels(request.getTaskLabelIds()));

        if (request.getAssigneeId() != null) {
            task.setAssignee(findAssignee(request.getAssigneeId()));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(Long id, TaskUpdateRequest request) {
        var task = findTask(id);

        if (request.isIndexPresent()) {
            task.setIndex(request.getIndex());
        }

        if (request.isAssigneeIdPresent()) {
            task.setAssignee(request.getAssigneeId() == null
                    ? null
                    : findAssignee(request.getAssigneeId()));
        }

        if (request.isTitlePresent()) {
            task.setName(request.getTitle());
        }

        if (request.isContentPresent()) {
            task.setDescription(request.getContent());
        }

        if (request.isStatusPresent()) {
            task.setTaskStatus(findTaskStatus(request.getStatus()));
        }

        if (request.isTaskLabelIdsPresent()) {
            task.setLabels(findLabels(request.getTaskLabelIds()));
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.delete(findTask(id));
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private TaskStatus findTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
    }

    private User findAssignee(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
    }

    private LinkedHashSet<Label> findLabels(List<Long> ids) {
        var labelsById = labelRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Label::getId, Function.identity()));

        return ids.stream()
                .map(id -> {
                    var label = labelsById.get(id);
                    if (label == null) {
                        throw new ResourceNotFoundException("Label not found");
                    }
                    return label;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
