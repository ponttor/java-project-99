package hexlet.code.app.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record UserResponse(Long id, String email, String firstName, String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt) {
}
