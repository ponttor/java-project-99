package hexlet.code.app.dto.label;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record LabelResponse(Long id, String name, @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt) {
}
