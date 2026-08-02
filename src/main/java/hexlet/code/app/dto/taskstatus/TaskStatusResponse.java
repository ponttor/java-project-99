package hexlet.code.app.dto.taskstatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusResponse {

    private Long id;

    private String name;

    private String slug;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}
