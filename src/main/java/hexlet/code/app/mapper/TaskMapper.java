package hexlet.code.app.mapper;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class TaskMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels")
    public abstract TaskResponse toResponse(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Task toEntity(TaskCreateRequest request);

    public void update(TaskUpdateRequest request, @MappingTarget Task task) {
        if (request.isIndexPresent()) {
            task.setIndex(request.getIndex());
        }

        if (request.isTitlePresent()) {
            task.setName(request.getTitle());
        }

        if (request.isContentPresent()) {
            task.setDescription(request.getContent());
        }
    }

    public List<Long> mapLabelIds(Set<Label> labels) {
        return labels.stream().map(Label::getId).toList();
    }
}
