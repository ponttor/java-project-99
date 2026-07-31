package hexlet.code.app.util;

import java.util.Set;

import hexlet.code.app.dto.UserCreateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private static final String DEFAULT_PASSWORD = "password";

    private final PasswordEncoder passwordEncoder;

    public Label label(String name) {
        var label = new Label();
        label.setName(name);
        return label;
    }

    public TaskStatus taskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatus;
    }

    public Task task(
            String name,
            String description,
            TaskStatus taskStatus,
            User assignee,
            Integer index
    ) {
        var task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(taskStatus);
        task.setAssignee(assignee);
        task.setIndex(index);
        return task;
    }

    public Task task(String name, TaskStatus taskStatus) {
        return task(name, null, taskStatus, null, null);
    }

    public Task taskWithLabels(String name, TaskStatus taskStatus, User assignee, Label... labels) {
        var task = task(name, null, taskStatus, assignee, null);
        task.setLabels(Set.of(labels));
        return task;
    }

    public User user(String email) {
        return user(email, null, null, DEFAULT_PASSWORD);
    }

    public User user(String email, String firstName, String lastName, String password) {
        var user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    public User userWithPassword(String email, String password) {
        return user(email, null, null, password);
    }

    public UserCreateRequest userCreateRequest(
            String email,
            String firstName,
            String lastName,
            String password
    ) {
        var request = new UserCreateRequest();
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPassword(password);
        return request;
    }
}
