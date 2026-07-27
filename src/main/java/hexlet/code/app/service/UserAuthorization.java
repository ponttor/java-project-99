package hexlet.code.app.service;

import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthorization {

    private final UserRepository userRepository;

    public boolean isOwner(Long userId, String email) {
        return userRepository.existsByIdAndEmail(userId, email);
    }
}
