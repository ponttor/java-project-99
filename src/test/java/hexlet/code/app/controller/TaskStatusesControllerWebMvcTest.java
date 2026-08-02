package hexlet.code.app.controller;

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

import hexlet.code.app.dto.taskstatus.TaskStatusCreateRequest;
import hexlet.code.app.dto.taskstatus.TaskStatusResponse;
import hexlet.code.app.dto.taskstatus.TaskStatusUpdateRequest;
import hexlet.code.app.service.TaskStatusService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskStatusesController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskStatusesControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskStatusService taskStatusService;

    @Test
    void shouldRouteTaskStatusCrudOperations() throws Exception {
        var response = taskStatusResponse(1L, "Draft", "draft");
        when(taskStatusService.findAll()).thenReturn(List.of(response));
        when(taskStatusService.findById(1L)).thenReturn(response);
        when(taskStatusService.create(any(TaskStatusCreateRequest.class))).thenReturn(response);
        when(taskStatusService.update(any(Long.class), any(TaskStatusUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"));
        mockMvc.perform(get("/api/task_statuses/{id}", 1)).andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("draft"));
        mockMvc.perform(post("/api/task_statuses").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Draft\",\"slug\":\"draft\"}")).andExpect(status().isCreated());
        mockMvc.perform(put("/api/task_statuses/{id}", 1).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Draft\"}")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/task_statuses/{id}", 1)).andExpect(status().isNoContent());

        verify(taskStatusService).delete(1L);
    }

    private TaskStatusResponse taskStatusResponse(Long id, String name, String slug) {
        return new TaskStatusResponse(id, name, slug, null);
    }
}
