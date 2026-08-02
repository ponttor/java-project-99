package hexlet.code.app.service;

import hexlet.code.app.dto.taskstatus.TaskStatusCreateRequest;
import hexlet.code.app.dto.taskstatus.TaskStatusResponse;
import hexlet.code.app.dto.taskstatus.TaskStatusUpdateRequest;
import hexlet.code.app.exception.ResourceConflictException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusResponse> findAll() {
        return taskStatusMapper.toResponses(taskStatusRepository.findAll());
    }

    public TaskStatusResponse findById(Long id) {
        return taskStatusMapper.toResponse(findTaskStatus(id));
    }

    public TaskStatusResponse create(TaskStatusCreateRequest request) {
        var taskStatus = taskStatusMapper.toEntity(request);
        return taskStatusMapper.toResponse(taskStatusRepository.save(taskStatus));
    }

    public TaskStatusResponse update(Long id, TaskStatusUpdateRequest request) {
        var taskStatus = findTaskStatus(id);

        taskStatusMapper.update(request, taskStatus);
        return taskStatusMapper.toResponse(taskStatusRepository.save(taskStatus));
    }

    public void delete(Long id) {
        try {
            taskStatusRepository.delete(findTaskStatus(id));
            taskStatusRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceConflictException("Task status is used by a task", exception);
        }
    }

    private TaskStatus findTaskStatus(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
    }
}
