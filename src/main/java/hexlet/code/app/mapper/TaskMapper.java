package hexlet.code.app.mapper;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels")
    TaskResponse toResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Task toEntity(TaskCreateRequest request);

    default List<Long> mapLabelIds(Set<Label> labels) {
        return labels.stream().map(Label::getId).toList();
    }
}
