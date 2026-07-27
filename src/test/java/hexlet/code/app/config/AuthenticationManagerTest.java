package hexlet.code.app.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthenticationManagerTest {

    private static final String EMAIL = "ivan@google.com";
    private static final String PASSWORD = "some-password";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        var user = new User();
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
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
