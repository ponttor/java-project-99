package hexlet.code.app.controller;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskFilterParams;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> index(@ParameterObject TaskFilterParams params) {
        var tasks = taskService.findAll(params);
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(tasks.size())).body(tasks);
    }

    @GetMapping("/{id}")
    public TaskResponse show(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskService.delete(id);
    }
}
