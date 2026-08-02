package hexlet.code.app.dto;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;

public final class PatchFieldDeserializer extends StdDeserializer<PatchField<?>> {

    private final JavaType valueType;

    public PatchFieldDeserializer() {
        this(null);
    }

    private PatchFieldDeserializer(JavaType valueType) {
        super(PatchField.class);
        this.valueType = valueType;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        return new PatchFieldDeserializer(property.getType().containedTypeOrUnknown(0));
    }

    @Override
    public PatchField<?> deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        return PatchField.of(context.readValue(parser, valueType));
    }

    @Override
    public PatchField<?> getNullValue(DeserializationContext context) {
        return PatchField.of(null);
    }
}
