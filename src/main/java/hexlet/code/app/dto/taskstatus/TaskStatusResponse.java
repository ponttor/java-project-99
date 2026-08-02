package hexlet.code.app.dto.taskstatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TaskStatusResponse(Long id, String name, String slug,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt) {
}
