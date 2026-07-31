package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.component.DataInitializer;
import hexlet.code.app.dto.TaskStatusCreateRequest;
import hexlet.code.app.dto.TaskStatusUpdateRequest;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void shouldShowTaskStatus() throws Exception {
        var taskStatus = taskStatusRepository.save(buildTaskStatus("To Review", "to_review"));

        mockMvc.perform(get("/api/task_statuses/{id}", taskStatus.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("To Review"))
                .andExpect(jsonPath("$.slug").value("to_review"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithAnonymousUser
    void shouldListTaskStatuses() throws Exception {
        taskStatusRepository.save(buildTaskStatus("Draft", "draft"));
        taskStatusRepository.save(buildTaskStatus("Published", "published"));

        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].slug").value("draft"))
                .andExpect(jsonPath("$[1].slug").value("published"));
    }

    @Test
    @WithMockUser
    void shouldCreateTaskStatus() throws Exception {
        var request = new TaskStatusCreateRequest();
        request.setName("New");
        request.setSlug("new");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.slug").value("new"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser
    void shouldUpdateTaskStatusPartially() throws Exception {
        var taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));
        var request = new TaskStatusUpdateRequest();
        request.setName("New status");

        mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("New status"))
                .andExpect(jsonPath("$.slug").value("draft"));
    }

    @Test
    @WithMockUser
    void shouldDeleteTaskStatus() throws Exception {
        var taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));

        mockMvc.perform(delete("/api/task_statuses/{id}", taskStatus.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(taskStatus.getId())).isEmpty();
    }

    @Test
    void shouldInitializeDefaultTaskStatuses() {
        dataInitializer.run();
        dataInitializer.run();

        assertThat(taskStatusRepository.findAll())
                .extracting(TaskStatus::getSlug)
                .containsExactlyInAnyOrder(
                        "draft",
                        "to_review",
                        "to_be_fixed",
                        "to_publish",
                        "published"
                );
    }

    @Test
    void shouldInitializeDefaultSlugWhenDefaultNameIsTaken() {
        taskStatusRepository.save(buildTaskStatus("Draft", "custom_draft"));

        dataInitializer.run();

        assertThat(taskStatusRepository.findBySlug("draft")).isPresent();
    }

    @Test
    @WithAnonymousUser
    void shouldRequireAuthenticationForMutations() throws Exception {
        var createRequest = new TaskStatusCreateRequest();
        createRequest.setName("New");
        createRequest.setSlug("new");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/task_statuses/{id}", 999)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/task_statuses/{id}", 999))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void shouldRejectInvalidTaskStatusData() throws Exception {
        var createRequest = new TaskStatusCreateRequest();
        createRequest.setName("");
        createRequest.setSlug("");

        mockMvc.perform(post("/api/task_statuses")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        var taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));
        var updateRequest = new TaskStatusUpdateRequest();
        updateRequest.setSlug("");

        mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldRejectExplicitNullOnUpdate() throws Exception {
        var taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));

        mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .contentType("application/json")
                        .content("{\"name\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnNotFoundForMissingTaskStatus() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindTaskStatusBySlug() {
        var taskStatus = taskStatusRepository.save(buildTaskStatus("Draft", "draft"));

        assertThat(taskStatusRepository.findBySlug("draft"))
                .get()
                .extracting(TaskStatus::getId)
                .isEqualTo(taskStatus.getId());
    }

    @Test
    void shouldRequireUniqueTaskStatusName() {
        taskStatusRepository.saveAndFlush(buildTaskStatus("Draft", "draft"));

        assertThatThrownBy(() ->
                taskStatusRepository.saveAndFlush(buildTaskStatus("Draft", "another_slug"))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldRequireUniqueTaskStatusSlug() {
        taskStatusRepository.saveAndFlush(buildTaskStatus("Draft", "draft"));

        assertThatThrownBy(() ->
                taskStatusRepository.saveAndFlush(buildTaskStatus("Another name", "draft"))
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    private TaskStatus buildTaskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatus;
    }
}
