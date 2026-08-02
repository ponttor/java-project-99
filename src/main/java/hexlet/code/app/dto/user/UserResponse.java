package hexlet.code.app.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}
