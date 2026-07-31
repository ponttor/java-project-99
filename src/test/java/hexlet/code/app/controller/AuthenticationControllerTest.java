package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    private static final String EMAIL = "ivan@google.com";
    private static final String PASSWORD = "some-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        var user = new User();
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
    }

    @Test
    void shouldReturnTokenForValidCredentials() throws Exception {
        var response = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var jwt = jwtDecoder.decode(response);
        assertThat(jwt.getSubject()).isEqualTo(EMAIL);
    }

    @Test
    void shouldReturnUnauthorizedForIncorrectPassword() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "wrong-password"
                                }
                                """.formatted(EMAIL)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForUnknownEmail() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "unknown@google.com",
                                    "password": "%s"
                                }
                                """.formatted(PASSWORD)))
                .andExpect(status().isUnauthorized());
    }
}
