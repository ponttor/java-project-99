package hexlet.code.app.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import hexlet.code.app.dto.user.UserCreateRequest;
import hexlet.code.app.dto.user.UserUpdateRequest;
import hexlet.code.app.model.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class UserMapperUnitTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void shouldMapCreateRequestAndEntity() {
        var request = Instancio.of(UserCreateRequest.class).set(field(UserCreateRequest::getEmail), "user@example.com")
                .set(field(UserCreateRequest::getFirstName), "John").set(field(UserCreateRequest::getLastName), "Doe")
                .create();

        var user = userMapper.toEntity(request);
        var response = userMapper.toResponse(user);

        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldUpdateEveryPresentField() {
        var user = Instancio.create(User.class);
        var request = Instancio.createBlank(UserUpdateRequest.class);
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

        var user = Instancio.of(User.class).ignore(field(User::getEmail)).create();
        userMapper.update(null, user);
        assertThat(user.getEmail()).isNull();
    }
}
