package hexlet.code.app.dto.taskstatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatusUpdateRequest {

    @Size(min = 1) private String name;

    @Size(min = 1) private String slug;

    @JsonSetter(nulls = Nulls.FAIL)
    public void setName(String name) {
        this.name = name;
    }

    @JsonSetter(nulls = Nulls.FAIL)
    public void setSlug(String slug) {
        this.slug = slug;
    }
}
