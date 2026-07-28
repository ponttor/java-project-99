package hexlet.code.app.service;

import java.util.List;

import hexlet.code.app.dto.TaskStatusCreateRequest;
import hexlet.code.app.dto.TaskStatusResponse;
import hexlet.code.app.dto.TaskStatusUpdateRequest;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public List<TaskStatusResponse> findAll() {
        return taskStatusRepository.findAll().stream()
                .map(TaskStatusResponse::new)
                .toList();
    }

    public TaskStatusResponse findById(Long id) {
        return new TaskStatusResponse(findTaskStatus(id));
    }

    public TaskStatusResponse create(TaskStatusCreateRequest request) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(request.getName());
        taskStatus.setSlug(request.getSlug());
        return new TaskStatusResponse(taskStatusRepository.save(taskStatus));
    }

    public TaskStatusResponse update(Long id, TaskStatusUpdateRequest request) {
        var taskStatus = findTaskStatus(id);

        if (request.getName() != null) {
            taskStatus.setName(request.getName());
        }

        if (request.getSlug() != null) {
            taskStatus.setSlug(request.getSlug());
        }

        return new TaskStatusResponse(taskStatusRepository.save(taskStatus));
    }

    public void delete(Long id) {
        try {
            taskStatusRepository.delete(findTaskStatus(id));
            taskStatusRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task status is used by a task", exception);
        }
    }

    private TaskStatus findTaskStatus(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
    }
}
