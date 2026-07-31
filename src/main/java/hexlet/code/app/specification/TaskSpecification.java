package hexlet.code.app.specification;

import java.util.Locale;

import hexlet.code.app.dto.TaskFilterParams;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskFilterParams params) {
        return withTitleContaining(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleContaining(String title) {
        return (root, query, criteriaBuilder) -> title == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + title.toLowerCase(Locale.ROOT) + "%"
                );
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, criteriaBuilder) -> assigneeId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, criteriaBuilder) -> status == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("taskStatus").get("slug"), status);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, criteriaBuilder) -> {
            if (labelId == null) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);
            return criteriaBuilder.equal(root.join("labels").get("id"), labelId);
        };
    }
}
