package hexlet.code.app.util;

import static org.instancio.Select.field;

import hexlet.code.app.dto.user.UserCreateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class ModelGenerator {

    private static final String DEFAULT_PASSWORD = "password";

    private final PasswordEncoder passwordEncoder;

    private Model<Label> labelModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<User> userModel;
    private Model<UserCreateRequest> userCreateRequestModel;

    @PostConstruct
    private void init() {
        labelModel = Instancio.of(Label.class).ignore(field(Label::getId)).ignore(field(Label::getTasks))
                .ignore(field(Label::getCreatedAt)).toModel();

        taskStatusModel = Instancio.of(TaskStatus.class).ignore(field(TaskStatus::getId))
                .ignore(field(TaskStatus::getCreatedAt)).toModel();

        taskModel = Instancio.of(Task.class).ignore(field(Task::getId)).ignore(field(Task::getCreatedAt))
                .set(field(Task::getIndex), null).set(field(Task::getDescription), null)
                .set(field(Task::getTaskStatus), null).set(field(Task::getAssignee), null)
                .set(field(Task::getLabels), Set.of()).toModel();

        userModel = Instancio.of(User.class).ignore(field(User::getId)).ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt)).set(field(User::getFirstName), null)
                .set(field(User::getLastName), null)
                .supply(field(User::getPassword), () -> passwordEncoder.encode(DEFAULT_PASSWORD)).toModel();

        userCreateRequestModel = Instancio.of(UserCreateRequest.class).toModel();
    }

    public Label label(String name) {
        return Instancio.of(labelModel).set(field(Label::getName), name).create();
    }

    public TaskStatus taskStatus(String name, String slug) {
        return Instancio.of(taskStatusModel).set(field(TaskStatus::getName), name).set(field(TaskStatus::getSlug), slug)
                .create();
    }

    public Task task(String name, String description, TaskStatus taskStatus, User assignee, Integer index) {
        return Instancio.of(taskModel).set(field(Task::getName), name).set(field(Task::getDescription), description)
                .set(field(Task::getTaskStatus), taskStatus).set(field(Task::getAssignee), assignee)
                .set(field(Task::getIndex), index).create();
    }

    public Task task(String name, TaskStatus taskStatus) {
        return task(name, null, taskStatus, null, null);
    }

    public Task taskWithLabels(String name, TaskStatus taskStatus, User assignee, Label... labels) {
        return Instancio.of(taskModel).set(field(Task::getName), name).set(field(Task::getTaskStatus), taskStatus)
                .set(field(Task::getAssignee), assignee).set(field(Task::getLabels), Set.of(labels)).create();
    }

    public User user(String email) {
        return Instancio.of(userModel).set(field(User::getEmail), email).create();
    }

    public User user(String email, String firstName, String lastName, String password) {
        return Instancio.of(userModel).set(field(User::getEmail), email).set(field(User::getFirstName), firstName)
                .set(field(User::getLastName), lastName).set(field(User::getPassword), passwordEncoder.encode(password))
                .create();
    }

    public User userWithPassword(String email, String password) {
        return user(email, null, null, password);
    }

    public UserCreateRequest userCreateRequest(String email, String firstName, String lastName, String password) {
        return Instancio.of(userCreateRequestModel).set(field(UserCreateRequest::getEmail), email)
                .set(field(UserCreateRequest::getFirstName), firstName)
                .set(field(UserCreateRequest::getLastName), lastName)
                .set(field(UserCreateRequest::getPassword), password).create();
    }
}
