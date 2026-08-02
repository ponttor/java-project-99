package hexlet.code.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.JwtService;
import hexlet.code.app.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserAuthorizationTest {

    private static final String OWNER_EMAIL = "owner@google.com";
    private static final String OTHER_EMAIL = "other@google.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(testDataFactory.user(OTHER_EMAIL));
    }

    @Test
    void shouldForbidUpdatingAnotherUser() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));
        var request = new UserUpdateRequest();
        request.setFirstName("Changed");

        mockMvc.perform(put("/api/users/{id}", owner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(OTHER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToUpdateThemself() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));
        var request = new UserUpdateRequest();
        request.setFirstName("Changed");

        mockMvc.perform(put("/api/users/{id}", owner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(OWNER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Changed"));
    }

    @Test
    void shouldForbidDeletingAnotherUser() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));

        mockMvc.perform(delete("/api/users/{id}", owner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(OTHER_EMAIL)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToDeleteThemself() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));

        mockMvc.perform(delete("/api/users/{id}", owner.getId())
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(OWNER_EMAIL)))
                .andExpect(status().isNoContent());
    }

    private String bearerToken(String email) {
        return "Bearer " + jwtService.generateToken(email);
    }
}
