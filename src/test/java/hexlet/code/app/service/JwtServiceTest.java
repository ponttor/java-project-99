package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hexlet.code.app.config.JwtConfig;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class JwtServiceTest {

    private static final String EMAIL = "ivan@google.com";
    private static final String SECRET = "local-test-secret-key-with-at-least-thirty-two-bytes";

    private JwtService jwtService;

    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        var jwtConfig = new JwtConfig();
        var secretKey = jwtConfig.jwtSecretKey(SECRET);
        jwtService = new JwtService(jwtConfig.jwtEncoder(secretKey));
        jwtDecoder = jwtConfig.jwtDecoder(secretKey);
    }

    @Test
    void shouldGenerateSignedTokenWithEmailAndExpiration() {
        var token = jwtService.generateToken(EMAIL);

        var jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo(EMAIL);
        assertThat(jwt.getIssuedAt()).isNotNull();
        assertThat(jwt.getExpiresAt()).isNotNull();
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void shouldRejectTokenWithModifiedSignature() {
        var token = jwtService.generateToken(EMAIL);
        var parts = token.split("\\.");
        var replacement = parts[2].startsWith("A") ? "B" : "A";
        parts[2] = replacement + parts[2].substring(1);
        var modifiedToken = String.join(".", parts);

        assertThatThrownBy(() -> jwtDecoder.decode(modifiedToken)).isInstanceOf(JwtException.class);
    }

    @Test
    void shouldRejectSecretShorterThanThirtyTwoBytes() {
        var jwtConfig = new JwtConfig();

        assertThatThrownBy(() -> jwtConfig.jwtSecretKey("too-short")).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT secret must contain at least 32 bytes");
    }
}
