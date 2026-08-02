package hexlet.code.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
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
                        .with(jwtFor(OTHER_EMAIL))
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
                        .with(jwtFor(OWNER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Changed"));
    }

    @Test
    void shouldForbidDeletingAnotherUser() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));

        mockMvc.perform(delete("/api/users/{id}", owner.getId())
                        .with(jwtFor(OTHER_EMAIL)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToDeleteThemself() throws Exception {
        var owner = userRepository.save(testDataFactory.user(OWNER_EMAIL));

        mockMvc.perform(delete("/api/users/{id}", owner.getId())
                        .with(jwtFor(OWNER_EMAIL)))
                .andExpect(status().isNoContent());
    }

    private JwtRequestPostProcessor jwtFor(String email) {
        return jwt().jwt(token -> token.subject(email));
    }
}
