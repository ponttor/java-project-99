package hexlet.code.app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import hexlet.code.app.model.TaskStatus;
import lombok.Getter;

@Getter
public class TaskStatusResponse {

    private final Long id;

    private final String name;

    private final String slug;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime createdAt;

    public TaskStatusResponse(TaskStatus taskStatus) {
        this.id = taskStatus.getId();
        this.name = taskStatus.getName();
        this.slug = taskStatus.getSlug();
        this.createdAt = taskStatus.getCreatedAt();
    }
}
