package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.ObjectMapper;
import hexlet.code.app.component.DataInitializer;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "hexlet@example.com")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void shouldRequireAuthenticationForUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateUser() throws Exception {
        var request = testDataFactory.userCreateRequest("jack@google.com", "Jack", "Jons", "some-password");

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("jack@google.com"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("Jons"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.password").doesNotExist());

        var user = userRepository.findAll().getFirst();
        assertThat(user.getPassword()).isNotEqualTo("some-password");
        assertThat(passwordEncoder.matches("some-password", user.getPassword())).isTrue();
    }

    @Test
    void shouldShowUser() throws Exception {
        var user = userRepository.save(testDataFactory.user("john@google.com", "John", "Doe", "password"));

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("john@google.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundForMissingUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User not found"));
    }

    @Test
    void shouldListUsers() throws Exception {
        userRepository.save(testDataFactory.user("john@google.com", "John", "Doe", "password"));
        userRepository.save(testDataFactory.user("jack@yahoo.com", "Jack", "Jons", "password"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("john@google.com"))
                .andExpect(jsonPath("$[1].email").value("jack@yahoo.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "jack@google.com")
    void shouldUpdateUserPartially() throws Exception {
        var user = userRepository.save(testDataFactory.user("jack@google.com", "Jack", "Jons", "password"));

        var request = new UserUpdateRequest();
        request.setEmail("jack@yahoo.com");
        request.setPassword("new-password");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("jack@yahoo.com"))
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.lastName").value("Jons"))
                .andExpect(jsonPath("$.password").doesNotExist());

        var updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("new-password", updatedUser.getPassword())).isTrue();
    }

    @Test
    @WithMockUser(username = "john@google.com")
    void shouldDeleteUser() throws Exception {
        var user = userRepository.save(testDataFactory.user("john@google.com", "John", "Doe", "password"));

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void shouldReturnBadRequestForInvalidCreateRequest() throws Exception {
        var request = testDataFactory.userCreateRequest("invalid-email", "Jack", "Jons", "12");

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidUpdateRequest() throws Exception {
        var user = userRepository.save(testDataFactory.user("john@google.com", "John", "Doe", "password"));

        var request = new UserUpdateRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldInitializeAdminUser() {
        dataInitializer.run();

        assertThat(userRepository.existsByEmail("hexlet@example.com")).isTrue();

        var admin = userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals("hexlet@example.com"))
                .findFirst()
                .orElseThrow();

        assertThat(admin.getPassword()).isNotEqualTo("qwerty");
        assertThat(passwordEncoder.matches("qwerty", admin.getPassword())).isTrue();
    }
}
