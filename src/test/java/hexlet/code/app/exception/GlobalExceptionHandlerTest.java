package hexlet.code.app.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHideAuthenticationFailureDetails() {
        var response = handler.handleAuthentication(new BadCredentialsException("User exists but password is wrong"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getDetail()).isEqualTo("Unauthorized");
    }

    @Test
    void shouldHideAccessDeniedDetails() {
        var response = handler.handleAccessDenied(new AccessDeniedException("Missing ADMIN role"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getDetail()).isEqualTo("Forbidden");
    }

    @Test
    void shouldHideUnexpectedExceptionDetails() {
        var response = handler.handleException(new IllegalStateException("Sensitive internal details"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getDetail()).isEqualTo("Internal Server Error");
    }
}
