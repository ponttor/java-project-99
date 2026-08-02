package hexlet.code.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hexlet.code.app.config.PasswordConfig;
import hexlet.code.app.config.SecurityConfig;
import hexlet.code.app.dto.user.UserResponse;
import hexlet.code.app.service.UserAuthorization;
import hexlet.code.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, PasswordConfig.class})
class UserAuthorizationTest {

    private static final String OWNER_EMAIL = "owner@google.com";
    private static final String OTHER_EMAIL = "other@google.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "userAuthorization")
    private UserAuthorization userAuthorization;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldForbidUpdatingAnotherUser() throws Exception {
        when(userAuthorization.isOwner(1L, OTHER_EMAIL)).thenReturn(false);

        mockMvc.perform(put("/api/users/{id}", 1).with(jwtFor(OTHER_EMAIL)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Changed\"}")).andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldAllowUserToUpdateThemself() throws Exception {
        var response = new UserResponse();
        response.setFirstName("Changed");
        when(userAuthorization.isOwner(1L, OWNER_EMAIL)).thenReturn(true);
        when(userService.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", 1).with(jwtFor(OWNER_EMAIL)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Changed\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Changed"));
    }

    @Test
    void shouldForbidDeletingAnotherUser() throws Exception {
        when(userAuthorization.isOwner(1L, OTHER_EMAIL)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", 1).with(jwtFor(OTHER_EMAIL))).andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    void shouldAllowUserToDeleteThemself() throws Exception {
        when(userAuthorization.isOwner(1L, OWNER_EMAIL)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", 1).with(jwtFor(OWNER_EMAIL))).andExpect(status().isNoContent());
    }

    private JwtRequestPostProcessor jwtFor(String email) {
        return jwt().jwt(token -> token.subject(email));
    }
}
