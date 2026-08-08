package hexlet.code.app.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateRequest {

    private JsonNullable<Integer> index = JsonNullable.undefined();

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();

    @NotBlank private JsonNullable<String> title = JsonNullable.undefined();

    private JsonNullable<String> content = JsonNullable.undefined();

    @NotBlank private JsonNullable<String> status = JsonNullable.undefined();

    @NotNull private JsonNullable<List<Long>> taskLabelIds = JsonNullable.undefined();
}
