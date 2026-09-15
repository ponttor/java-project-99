package hexlet.code.app.service;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskFilterParams;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import java.util.List;

public interface TaskService {

    List<TaskResponse> findAll(TaskFilterParams params);

    TaskResponse findById(Long id);

    TaskResponse create(TaskCreateRequest request);

    TaskResponse update(Long id, TaskUpdateRequest request);

    TaskResponse replace(Long id, TaskCreateRequest request);

    void delete(Long id);
}
