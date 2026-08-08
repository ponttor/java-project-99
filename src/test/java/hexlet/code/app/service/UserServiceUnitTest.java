package hexlet.code.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hexlet.code.app.dto.user.UserCreateRequest;
import hexlet.code.app.dto.user.UserResponse;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldEncodePasswordWhenCreatingUser() {
        var request = Instancio.createBlank(UserCreateRequest.class);
        request.setPassword("plain-password");
        var user = Instancio.create(User.class);
        var response = Instancio.create(UserResponse.class);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        var result = userService.create(request);

        assertThat(result).isSameAs(response);
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        verify(passwordEncoder).encode("plain-password");
    }

    @Test
    void shouldEncodeNewPasswordWhenUpdatingUser() {
        var request = Instancio.createBlank(UserUpdateRequest.class);
        request.setPassword("new-password");
        var user = Instancio.create(User.class);
        var response = Instancio.create(UserResponse.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("new-encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        var result = userService.update(1L, request);

        assertThat(result).isSameAs(response);
        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        verify(userMapper).update(request, user);
        verify(passwordEncoder).encode("new-password");
    }
}
