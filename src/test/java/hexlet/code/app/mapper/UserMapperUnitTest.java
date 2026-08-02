package hexlet.code.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.app.dto.user.UserCreateRequest;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.model.User;
import org.junit.jupiter.api.Test;

class UserMapperUnitTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void shouldMapCreateRequestAndEntity() {
        var request = new UserCreateRequest();
        request.setEmail("user@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        var user = userMapper.toEntity(request);
        var response = userMapper.toResponse(user);

        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldUpdateEveryPresentField() {
        var user = new User();
        var request = new UserUpdateRequest();
        request.setEmail("updated@example.com");
        request.setFirstName("Updated");
        request.setLastName("User");

        userMapper.update(request, user);

        assertThat(user.getEmail()).isEqualTo("updated@example.com");
        assertThat(user.getFirstName()).isEqualTo("Updated");
        assertThat(user.getLastName()).isEqualTo("User");
    }

    @Test
    void shouldHandleNullSources() {
        assertThat(userMapper.toEntity(null)).isNull();
        assertThat(userMapper.toResponse(null)).isNull();

        var user = new User();
        userMapper.update(null, user);
        assertThat(user.getEmail()).isNull();
    }
}
