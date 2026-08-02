package hexlet.code.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.service.JwtService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRouteLoginThroughAuthenticationManagerAndJwtService() throws Exception {
        var authentication = UsernamePasswordAuthenticationToken.authenticated("user@example.com", null, List.of());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken("user@example.com")).thenReturn("signed.jwt.token");

        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "username": "user@example.com",
                  "password": "password"
                }
                """)).andExpect(status().isOk()).andExpect(content().string("signed.jwt.token"));

        var captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getPrincipal()).isEqualTo("user@example.com");
        assertThat(captor.getValue().getCredentials()).isEqualTo("password");
        verify(jwtService).generateToken("user@example.com");
    }
}
