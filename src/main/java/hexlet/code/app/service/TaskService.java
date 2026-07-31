package hexlet.code.app.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import hexlet.code.app.dto.TaskCreateRequest;
import hexlet.code.app.dto.TaskResponse;
import hexlet.code.app.dto.TaskUpdateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserRepository userRepository;

    private final LabelRepository labelRepository;

    public List<TaskResponse> findAll(Long labelId) {
        var tasks = labelId == null
                ? taskRepository.findAll()
                : taskRepository.findAllByLabelsId(labelId);

        return tasks.stream()
                .map(TaskResponse::new)
                .toList();
    }

    public TaskResponse findById(Long id) {
        return new TaskResponse(findTask(id));
    }

    public TaskResponse create(TaskCreateRequest request) {
        var task = new Task();
        task.setIndex(request.getIndex());
        task.setName(request.getTitle());
        task.setDescription(request.getContent());
        task.setTaskStatus(findTaskStatus(request.getStatus()));
        task.setLabels(findLabels(request.getTaskLabelIds()));

        if (request.getAssigneeId() != null) {
            task.setAssignee(findAssignee(request.getAssigneeId()));
        }

        return new TaskResponse(taskRepository.save(task));
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

        return new TaskResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.delete(findTask(id));
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private TaskStatus findTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
    }

    private User findAssignee(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }

    private LinkedHashSet<Label> findLabels(List<Long> ids) {
        var labelsById = labelRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Label::getId, Function.identity()));

        return ids.stream()
                .map(id -> {
                    var label = labelsById.get(id);
                    if (label == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found");
                    }
                    return label;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
