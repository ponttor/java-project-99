package hexlet.code.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateRequest {

    private Integer index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotNull
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    private String status;

    private List<Long> taskLabelIds = new ArrayList<>();

    @JsonSetter(nulls = Nulls.FAIL)
    public void setTaskLabelIds(List<Long> taskLabelIds) {
        this.taskLabelIds = taskLabelIds;
    }
}
