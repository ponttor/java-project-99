package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskFilterParams;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.service.TaskService;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TasksController.class)
@AutoConfigureMockMvc(addFilters = false)
class TasksControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldBindFiltersAndRouteTaskIndex() throws Exception {
        var response = taskResponse(1L, "Task");
        when(taskService.findAll(any(TaskFilterParams.class))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/tasks").queryParam("titleCont", "task").queryParam("assigneeId", "3")
                .queryParam("status", "draft").queryParam("labelId", "5")).andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1")).andExpect(jsonPath("$[0].title").value("Task"));

        var captor = ArgumentCaptor.forClass(TaskFilterParams.class);
        verify(taskService).findAll(captor.capture());
        assertThat(captor.getValue().getTitleCont()).isEqualTo("task");
        assertThat(captor.getValue().getAssigneeId()).isEqualTo(3L);
        assertThat(captor.getValue().getStatus()).isEqualTo("draft");
        assertThat(captor.getValue().getLabelId()).isEqualTo(5L);
    }

    @Test
    void shouldRouteTaskMutationOperations() throws Exception {
        var response = taskResponse(1L, "Task");
        when(taskService.findById(1L)).thenReturn(response);
        when(taskService.create(any(TaskCreateRequest.class))).thenReturn(response);
        when(taskService.update(any(Long.class), any(TaskUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{id}", 1)).andExpect(status().isOk());
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Task\",\"status\":\"draft\",\"taskLabelIds\":[]}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/tasks/{id}", 1).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated task\"}")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/tasks/{id}", 1)).andExpect(status().isNoContent());

        verify(taskService).delete(1L);
    }

    private TaskResponse taskResponse(Long id, String title) {
        var response = Instancio.createBlank(TaskResponse.class);
        response.setId(id);
        response.setTitle(title);
        return response;
    }
}
