package hexlet.code.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import hexlet.code.app.dto.user.UserCreateRequest;
import hexlet.code.app.dto.user.UserResponse;
import hexlet.code.app.exception.GlobalExceptionHandler;
import hexlet.code.app.exception.ResourceConflictException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UsersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRouteIndexAndExposeTotalCount() throws Exception {
        var first = userResponse(1L, "first@example.com");
        var second = userResponse(2L, "second@example.com");
        when(userService.findAll()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$[0].email").value("first@example.com"))
                .andExpect(jsonPath("$[1].email").value("second@example.com"));

        verify(userService).findAll();
    }

    @Test
    void shouldRejectInvalidCreateRequestBeforeCallingService() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": "12"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnNotFoundProblemDetail() throws Exception {
        when(userService.findById(999L))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User not found"));
    }

    @Test
    void shouldRenderConflictProblemDetail() throws Exception {
        when(userService.create(any(UserCreateRequest.class)))
                .thenThrow(new ResourceConflictException("Resource conflict"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Resource conflict"));
    }

    private UserResponse userResponse(Long id, String email) {
        var response = new UserResponse();
        response.setId(id);
        response.setEmail(email);
        return response;
    }
}
