package hexlet.code.app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.app.model.Task;
import lombok.Getter;

@Getter
public class TaskResponse {

    private final Long id;

    private final Integer index;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime createdAt;

    @JsonProperty("assignee_id")
    private final Long assigneeId;

    private final String title;

    private final String content;

    private final String status;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.index = task.getIndex();
        this.createdAt = task.getCreatedAt();
        this.assigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();
        this.title = task.getName();
        this.content = task.getDescription();
        this.status = task.getTaskStatus().getSlug();
    }
}
