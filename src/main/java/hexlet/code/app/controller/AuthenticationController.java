package hexlet.code.app.controller;

import hexlet.code.app.dto.auth.AuthRequest;
import hexlet.code.app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        var authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(),
                request.getPassword());
        var authentication = authenticationManager.authenticate(authenticationRequest);

        return jwtService.generateToken(authentication.getName());
    }
}
