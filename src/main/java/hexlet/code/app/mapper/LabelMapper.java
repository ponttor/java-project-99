package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelCreateRequest;
import hexlet.code.app.dto.LabelResponse;
import hexlet.code.app.dto.LabelUpdateRequest;
import hexlet.code.app.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabelMapper {

    LabelResponse toResponse(Label label);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Label toEntity(LabelCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(LabelUpdateRequest request, @MappingTarget Label label);
}
