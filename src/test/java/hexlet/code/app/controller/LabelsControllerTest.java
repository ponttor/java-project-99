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
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class LabelsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
    }

    @Test
    void shouldCreateShowAndListLabels() throws Exception {
        var request = Map.of("name", "new label");

        var result = mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("new label"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn();

        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/labels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new label"));

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$", hasSize(1)));

        assertThat(labelRepository.findByName("new label")).isPresent();
    }

    @Test
    void shouldUpdateAndDeleteLabel() throws Exception {
        var label = labelRepository.save(buildLabel("old name"));

        mockMvc.perform(put("/api/labels/{id}", label.getId())
                        .contentType("application/json")
                        .content("{\"name\":\"new name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("new name"));

        mockMvc.perform(delete("/api/labels/{id}", label.getId()))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(label.getId())).isEmpty();
    }

    @Test
    void shouldRejectInvalidAndDuplicateNames() throws Exception {
        labelRepository.save(buildLabel("duplicate"));

        mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content("{\"name\":\"ab\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content("{\"name\":\"duplicate\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/labels/{id}", 999)
                        .contentType("application/json")
                        .content("{\"name\":\"valid name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldEnforceExactNameLengthBounds() throws Exception {
        var result = mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("name", "abc"))))
                .andExpect(status().isCreated())
                .andReturn();
        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        var maxLengthName = "x".repeat(1000);

        mockMvc.perform(put("/api/labels/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("name", maxLengthName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(maxLengthName));

        mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("name", "x".repeat(1001)))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void shouldRequireAuthenticationForAllLabelRoutes() throws Exception {
        mockMvc.perform(get("/api/labels")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/labels/{id}", 999)).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/labels")
                        .contentType("application/json")
                        .content("{\"name\":\"new label\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/labels/{id}", 999)
                        .contentType("application/json")
                        .content("{\"name\":\"new label\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/labels/{id}", 999)).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotDeleteLabelUsedByTask() throws Exception {
        var label = labelRepository.save(buildLabel("bug"));
        var status = taskStatusRepository.save(buildTaskStatus());
        var task = new Task();
        task.setName("Task");
        task.setTaskStatus(status);
        task.setLabels(java.util.Set.of(label));
        taskRepository.saveAndFlush(task);

        mockMvc.perform(delete("/api/labels/{id}", label.getId()))
                .andExpect(status().isConflict());

        assertThat(labelRepository.findById(label.getId())).isPresent();
    }

    @Test
    void shouldFindLabelByUniqueNameAndEnforceUniqueness() {
        var label = labelRepository.saveAndFlush(buildLabel("feature"));

        assertThat(labelRepository.findByName("feature"))
                .get()
                .extracting(Label::getId)
                .isEqualTo(label.getId());
        var duplicateLabel = buildLabel("feature");

        assertThatThrownBy(() -> labelRepository.saveAndFlush(duplicateLabel))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldInitializeDefaultLabelsIdempotently() {
        dataInitializer.run();
        dataInitializer.run();

        assertThat(labelRepository.findAll())
                .extracting(Label::getName)
                .containsExactlyInAnyOrder("feature", "bug");
    }

    private Label buildLabel(String name) {
        var label = new Label();
        label.setName(name);
        return label;
    }

    private TaskStatus buildTaskStatus() {
        var status = new TaskStatus();
        status.setName("Draft");
        status.setSlug("draft");
        return status;
    }
}
