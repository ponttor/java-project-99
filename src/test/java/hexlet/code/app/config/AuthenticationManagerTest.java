package hexlet.code.app.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AuthenticationManagerTest {

    private static final String EMAIL = "ivan@google.com";
    private static final String PASSWORD = "some-password";

    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        var passwordEncoder = new BCryptPasswordEncoder();
        var user = User.withUsername(EMAIL)
                .password(passwordEncoder.encode(PASSWORD))
                .authorities(java.util.List.of())
                .build();
        UserDetailsService userDetailsService = email -> {
            if (!EMAIL.equals(email)) {
                throw new UsernameNotFoundException("User not found");
            }
            return user;
        };
        var securityConfig = new SecurityConfig();
        var provider = securityConfig.authenticationProvider(userDetailsService, passwordEncoder);
        authenticationManager = securityConfig.authenticationManager(provider);
    }

    @Test
    void shouldAuthenticateUserByEmailAndPassword() {
        var request = UsernamePasswordAuthenticationToken.unauthenticated(EMAIL, PASSWORD);

        var authentication = authenticationManager.authenticate(request);

        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getName()).isEqualTo(EMAIL);
    }

    @Test
    void shouldRejectIncorrectPassword() {
        var request = UsernamePasswordAuthenticationToken.unauthenticated(EMAIL, "wrong-password");

        assertThatThrownBy(() -> authenticationManager.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldRejectUnknownEmail() {
        var request = UsernamePasswordAuthenticationToken.unauthenticated("unknown@google.com", PASSWORD);

        assertThatThrownBy(() -> authenticationManager.authenticate(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
