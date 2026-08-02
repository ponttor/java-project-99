package hexlet.code.app.mapper;

import hexlet.code.app.dto.task.TaskCreateRequest;
import hexlet.code.app.dto.task.TaskResponse;
import hexlet.code.app.dto.task.TaskUpdateRequest;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import java.util.List;
import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfiguration.class, uses = PatchFieldMapper.class)
public abstract class TaskMapper {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels")
    public abstract TaskResponse toResponse(Task task);

    public abstract List<TaskResponse> toResponses(List<Task> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Task toEntity(TaskCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void update(TaskUpdateRequest request, @MappingTarget Task task);

    public List<Long> mapLabelIds(Set<Label> labels) {
        return labels.stream().map(Label::getId).toList();
    }
}
