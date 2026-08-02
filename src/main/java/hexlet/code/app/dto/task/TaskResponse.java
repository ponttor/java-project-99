package hexlet.code.app.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record TaskResponse(Long id, Integer index, @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt,
        @JsonProperty("assignee_id") Long assigneeId, String title, String content, String status,
        List<Long> taskLabelIds) {
}
