package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAuthorization userAuthorization;

    @Test
    void shouldCheckUserIdAndEmailTogether() {
        when(userRepository.existsByIdAndEmail(1L, "owner@example.com")).thenReturn(true);

        assertThat(userAuthorization.isOwner(1L, "owner@example.com")).isTrue();
        assertThat(userAuthorization.isOwner(1L, "other@example.com")).isFalse();
    }
}
