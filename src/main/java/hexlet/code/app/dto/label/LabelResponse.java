package hexlet.code.app.dto.label;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelResponse {

    private Long id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
}
