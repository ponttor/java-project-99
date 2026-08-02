package hexlet.code.app.dto.task;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TaskUpdateRequest {

    private Integer index;

    private Long assigneeId;

    @Size(min = 1)
    private String title;

    private String content;

    @Size(min = 1)
    private String status;

    private List<Long> taskLabelIds;

    private boolean indexPresent;

    private boolean assigneeIdPresent;

    private boolean titlePresent;

    private boolean contentPresent;

    private boolean statusPresent;

    private boolean taskLabelIdsPresent;

    @JsonSetter
    public void setIndex(Integer index) {
        this.index = index;
        this.indexPresent = true;
    }

    @JsonSetter("assignee_id")
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        this.assigneeIdPresent = true;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setTitle(String title) {
        this.title = title;
        this.titlePresent = true;
    }

    @JsonSetter
    public void setContent(String content) {
        this.content = content;
        this.contentPresent = true;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setStatus(String status) {
        this.status = status;
        this.statusPresent = true;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setTaskLabelIds(List<Long> taskLabelIds) {
        this.taskLabelIds = taskLabelIds;
        this.taskLabelIdsPresent = true;
    }
}
