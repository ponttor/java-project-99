package hexlet.code.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String firstName;

    private String lastName;

    @NotBlank @Email private String email;

    @NotBlank @Size(min = 3) private String password;
}
