package hexlet.code.app.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import hexlet.code.app.dto.PatchField;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateRequest {

    private PatchField<Integer> index = PatchField.undefined();

    @JsonProperty("assignee_id")
    private PatchField<Long> assigneeId = PatchField.undefined();

    private PatchField<String> title = PatchField.undefined();

    private PatchField<String> content = PatchField.undefined();

    private PatchField<String> status = PatchField.undefined();

    private PatchField<List<Long>> taskLabelIds = PatchField.undefined();

    @AssertTrue(message = "title must be non-empty when present") public boolean isTitleValid() {
        return isNonEmpty(title);
    }

    @AssertTrue(message = "status must be non-empty when present") public boolean isStatusValid() {
        return isNonEmpty(status);
    }

    @AssertTrue(message = "taskLabelIds must not be null when present") public boolean isTaskLabelIdsValid() {
        return !taskLabelIds.isPresent() || taskLabelIds.orElse(null) != null;
    }

    private boolean isNonEmpty(PatchField<String> field) {
        var value = field.orElse(null);
        return !field.isPresent() || value != null && !value.isEmpty();
    }
}
