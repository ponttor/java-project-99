package hexlet.code.app.mapper;

import hexlet.code.app.dto.PatchField;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface PatchFieldMapper {

    @Condition
    default <T> boolean isPresent(PatchField<T> field) {
        return field != null && field.isPresent();
    }

    default <T> T unwrap(PatchField<T> field) {
        return field == null ? null : field.orElse(null);
    }
}
