package hexlet.code.app.dto.taskstatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateRequest {

    @NotBlank private String name;

    @NotBlank private String slug;
}
