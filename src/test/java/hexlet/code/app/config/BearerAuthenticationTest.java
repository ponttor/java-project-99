package hexlet.code.app.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BearerAuthenticationTest {

    private static final String EMAIL = "ivan@google.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldAccessProtectedRouteWithBearerToken() throws Exception {
        var token = jwtService.generateToken(EMAIL);

        mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidBearerToken() throws Exception {
        var token = jwtService.generateToken(EMAIL);
        var parts = token.split("\\.");
        var replacement = parts[2].startsWith("A") ? "B" : "A";
        parts[2] = replacement + parts[2].substring(1);
        var invalidToken = String.join(".", parts);

        mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
