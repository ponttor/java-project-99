package hexlet.code.app.service;

import hexlet.code.app.dto.taskstatus.TaskStatusCreateRequest;
import hexlet.code.app.dto.taskstatus.TaskStatusResponse;
import hexlet.code.app.dto.taskstatus.TaskStatusUpdateRequest;
import java.util.List;

public interface TaskStatusService {

    List<TaskStatusResponse> findAll();

    TaskStatusResponse findById(Long id);

    TaskStatusResponse create(TaskStatusCreateRequest request);

    TaskStatusResponse update(Long id, TaskStatusUpdateRequest request);

    void delete(Long id);
}
