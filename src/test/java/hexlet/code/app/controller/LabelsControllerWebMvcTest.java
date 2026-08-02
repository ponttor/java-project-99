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

import java.util.List;

import hexlet.code.app.dto.label.LabelCreateRequest;
import hexlet.code.app.dto.label.LabelResponse;
import hexlet.code.app.dto.label.LabelUpdateRequest;
import hexlet.code.app.service.LabelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LabelsController.class)
@AutoConfigureMockMvc(addFilters = false)
class LabelsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LabelService labelService;

    @Test
    void shouldRouteLabelCrudOperations() throws Exception {
        var response = labelResponse(1L, "feature");
        when(labelService.findAll()).thenReturn(List.of(response));
        when(labelService.findById(1L)).thenReturn(response);
        when(labelService.create(any(LabelCreateRequest.class))).thenReturn(response);
        when(labelService.update(any(Long.class), any(LabelUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"));
        mockMvc.perform(get("/api/labels/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("feature"));
        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"feature\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/labels/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"feature\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/labels/{id}", 1))
                .andExpect(status().isNoContent());

        verify(labelService).delete(1L);
    }

    private LabelResponse labelResponse(Long id, String name) {
        var response = new LabelResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }
}
