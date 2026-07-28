package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private TaskStatus taskStatus;

    private User assignee;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));
        assignee = userRepository.save(buildUser("worker@example.com"));
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldCreateTask() throws Exception {
        var request = Map.of(
                "index", 12,
                "assignee_id", assignee.getId(),
                "title", "Test title",
                "content", "Test content",
                "status", taskStatus.getSlug()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.index").value(12))
                .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldShowTask() throws Exception {
        var task = taskRepository.save(buildTask("Task 1", "Description", taskStatus, assignee, 3140));

        mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.index").value(3140))
                .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.content").value("Description"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldListTasks() throws Exception {
        taskRepository.save(buildTask("Task 1", "First", taskStatus, assignee, 1));
        taskRepository.save(buildTask("Task 2", "Second", taskStatus, null, null));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].assignee_id").isEmpty());
    }

    @Test
    void shouldUpdateTaskPartially() throws Exception {
        var task = taskRepository.save(buildTask("Old title", "Old content", taskStatus, assignee, 5));
        var published = taskStatusRepository.save(buildTaskStatus("Published", "published"));
        var newAssignee = userRepository.save(buildUser("new-worker@example.com"));
        var request = Map.of(
                "title", "New title",
                "status", published.getSlug(),
                "assignee_id", newAssignee.getId()
        );

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.content").value("Old content"))
                .andExpect(jsonPath("$.index").value(5))
                .andExpect(jsonPath("$.status").value("published"))
                .andExpect(jsonPath("$.assignee_id").value(newAssignee.getId()));
    }

    @Test
    void shouldClearOptionalTaskFields() throws Exception {
        var task = taskRepository.save(buildTask("Task", "Content", taskStatus, assignee, 5));

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content("""
                                {
                                    "index": null,
                                    "assignee_id": null,
                                    "content": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task"))
                .andExpect(jsonPath("$.index").isEmpty())
                .andExpect(jsonPath("$.assignee_id").isEmpty())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldDeleteTask() throws Exception {
        var task = taskRepository.save(buildTask("Task", null, taskStatus, null, null));

        mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    @WithAnonymousUser
    void shouldRequireAuthenticationForAllTaskRoutes() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tasks/{id}", 999))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/tasks/{id}", 999)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/tasks/{id}", 999))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectInvalidTaskData() throws Exception {
        var request = Map.of(
                "title", "",
                "status", taskStatus.getSlug()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        var task = taskRepository.save(buildTask("Task", null, taskStatus, null, null));
        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content("{\"title\":null}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("{\"title\":\"Task\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content("{\"title\":\"Task\",\"status\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForMissingTask() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/tasks/{id}", 999)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/tasks/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForMissingTaskRelations() throws Exception {
        var missingStatusRequest = Map.of(
                "title", "Task",
                "status", "missing"
        );
        var missingAssigneeRequest = Map.of(
                "title", "Task",
                "status", taskStatus.getSlug(),
                "assignee_id", 999
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(missingStatusRequest)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(missingAssigneeRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForMissingRelationsOnUpdate() throws Exception {
        var task = taskRepository.save(buildTask("Task", null, taskStatus, assignee, null));

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content("{\"status\":\"missing\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType("application/json")
                        .content("{\"assignee_id\":999}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAcceptLongTitleAndContent() throws Exception {
        var longTitle = "T".repeat(500);
        var longContent = "C".repeat(1000);
        var request = Map.of(
                "title", longTitle,
                "content", longContent,
                "status", taskStatus.getSlug()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(longTitle))
                .andExpect(jsonPath("$.content").value(longContent));
    }

    @Test
    void shouldCreateTaskWithoutOptionalFields() throws Exception {
        var request = Map.of(
                "title", "Minimal task",
                "status", taskStatus.getSlug()
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Minimal task"))
                .andExpect(jsonPath("$.index").isEmpty())
                .andExpect(jsonPath("$.assignee_id").isEmpty())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "worker@example.com")
    void shouldNotDeleteAssignedUser() throws Exception {
        taskRepository.saveAndFlush(buildTask("Task", null, taskStatus, assignee, null));

        mockMvc.perform(delete("/api/users/{id}", assignee.getId()))
                .andExpect(status().isConflict());

        assertThat(userRepository.findById(assignee.getId())).isPresent();
    }

    @Test
    void shouldNotDeleteUsedTaskStatus() throws Exception {
        taskRepository.saveAndFlush(buildTask("Task", null, taskStatus, null, null));

        mockMvc.perform(delete("/api/task_statuses/{id}", taskStatus.getId()))
                .andExpect(status().isConflict());

        assertThat(taskStatusRepository.findById(taskStatus.getId())).isPresent();
    }

    private TaskStatus buildTaskStatus(String name, String slug) {
        var status = new TaskStatus();
        status.setName(name);
        status.setSlug(slug);
        return status;
    }

    private User buildUser(String email) {
        var user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        return user;
    }

    private Task buildTask(
            String name,
            String description,
            TaskStatus status,
            User user,
            Integer index
    ) {
        var task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(status);
        task.setAssignee(user);
        task.setIndex(index);
        return task;
    }
}
