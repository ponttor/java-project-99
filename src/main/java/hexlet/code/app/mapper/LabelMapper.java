package hexlet.code.app.mapper;

import hexlet.code.app.dto.label.LabelCreateRequest;
import hexlet.code.app.dto.label.LabelResponse;
import hexlet.code.app.dto.label.LabelUpdateRequest;
import hexlet.code.app.model.Label;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface LabelMapper {

    LabelResponse toResponse(Label label);

    List<LabelResponse> toResponses(List<Label> labels);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Label toEntity(LabelCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(LabelUpdateRequest request, @MappingTarget Label label);
}
