package hexlet.code.app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import hexlet.code.app.model.Label;
import lombok.Getter;

@Getter
public class LabelResponse {

    private final Long id;

    private final String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime createdAt;

    public LabelResponse(Label label) {
        this.id = label.getId();
        this.name = label.getName();
        this.createdAt = label.getCreatedAt();
    }
}
