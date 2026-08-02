package hexlet.code.app.dto;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PatchFieldDeserializer.class)
public final class PatchField<T> {

    private static final PatchField<?> UNDEFINED = new PatchField<>(false, null);

    private final boolean present;

    private final T value;

    private PatchField(boolean present, T value) {
        this.present = present;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <T> PatchField<T> undefined() {
        return (PatchField<T>) UNDEFINED;
    }

    public static <T> PatchField<T> of(T value) {
        return new PatchField<>(true, value);
    }

    public boolean isPresent() {
        return present;
    }

    public T orElse(T fallback) {
        return present ? value : fallback;
    }
}
