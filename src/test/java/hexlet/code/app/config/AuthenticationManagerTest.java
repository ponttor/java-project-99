package hexlet.code.app.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@SpringBootTest
class AuthenticationManagerTest {

    private static final String EMAIL = "ivan@google.com";
    private static final String PASSWORD = "some-password";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userRepository.save(testDataFactory.userWithPassword(EMAIL, PASSWORD));
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
