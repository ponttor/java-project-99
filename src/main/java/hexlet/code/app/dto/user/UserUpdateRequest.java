package hexlet.code.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String firstName;

    private String lastName;

    @Size(min = 1) @Email private String email;

    @Size(min = 3) private String password;
}
