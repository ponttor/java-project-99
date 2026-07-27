package hexlet.code.app.service;

import java.time.Duration;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

    private final JwtEncoder jwtEncoder;

    public String generateToken(String email) {
        var now = Instant.now();
        var headers = JwsHeader.with(MacAlgorithm.HS256).build();
        var claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_LIFETIME))
                .subject(email)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims))
                .getTokenValue();
    }
}
