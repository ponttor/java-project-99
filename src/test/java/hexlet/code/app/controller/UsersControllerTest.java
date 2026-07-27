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

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.component.DataInitializer;
import hexlet.code.app.dto.UserCreateRequest;
import hexlet.code.app.dto.UserUpdateRequest;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
        var request = buildCreateRequest("jack@google.com", "Jack", "Jons", "some-password");

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
        var user = userRepository.save(buildUser("john@google.com", "John", "Doe", "password"));

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
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListUsers() throws Exception {
        userRepository.save(buildUser("john@google.com", "John", "Doe", "password"));
        userRepository.save(buildUser("jack@yahoo.com", "Jack", "Jons", "password"));

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
        var user = userRepository.save(buildUser("jack@google.com", "Jack", "Jons", "password"));

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
        var user = userRepository.save(buildUser("john@google.com", "John", "Doe", "password"));

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void shouldReturnBadRequestForInvalidCreateRequest() throws Exception {
        var request = buildCreateRequest("invalid-email", "Jack", "Jons", "12");

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidUpdateRequest() throws Exception {
        var user = userRepository.save(buildUser("john@google.com", "John", "Doe", "password"));

        var request = new UserUpdateRequest();
        request.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldInitializeAdminUser() throws Exception {
        dataInitializer.run();

        assertThat(userRepository.existsByEmail("hexlet@example.com")).isTrue();

        var admin = userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals("hexlet@example.com"))
                .findFirst()
                .orElseThrow();

        assertThat(admin.getPassword()).isNotEqualTo("qwerty");
        assertThat(passwordEncoder.matches("qwerty", admin.getPassword())).isTrue();
    }

    private UserCreateRequest buildCreateRequest(String email, String firstName, String lastName, String password) {
        var request = new UserCreateRequest();
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPassword(password);
        return request;
    }

    private User buildUser(
            String email,
            String firstName,
            String lastName,
            String password
    ) {
        var user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }
}
