package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@SpringBootTest
class JwtServiceTest {

    private static final String EMAIL = "ivan@google.com";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void shouldGenerateSignedTokenWithEmailAndExpiration() {
        var token = jwtService.generateToken(EMAIL);

        var jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo(EMAIL);
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isNotNull();
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()))
                .isEqualTo(Duration.ofHours(1));
    }

    @Test
    void shouldRejectTokenWithModifiedSignature() {
        var token = jwtService.generateToken(EMAIL);
        var parts = token.split("\\.");
        var replacement = parts[2].startsWith("A") ? "B" : "A";
        parts[2] = replacement + parts[2].substring(1);
        var modifiedToken = String.join(".", parts);

        assertThatThrownBy(() -> jwtDecoder.decode(modifiedToken))
                .isInstanceOf(JwtException.class);
    }
}
