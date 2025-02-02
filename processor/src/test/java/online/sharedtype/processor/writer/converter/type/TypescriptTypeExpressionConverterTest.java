package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class TypescriptTypeExpressionConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypescriptTypeExpressionConverter converter = new TypescriptTypeExpressionConverter(ctxMocks.getContext());

    @Test
    void typeContract() {
        assertThat(converter.typeNameMappings.keySet()).containsAll(Constants.STRING_AND_NUMBER_TYPES);
    }

    @Test
    void enumKeyWithPartialRecordMapSpec() {
        ConcreteTypeInfo enumTypeInfo = ConcreteTypeInfo.builder().simpleName("MyEnum").enumType(true).build();
        var mapSpec = converter.mapSpec(enumTypeInfo);
        assertThat(mapSpec.prefix).isEqualTo("Partial<Record<");
        assertThat(mapSpec.delimiter).isEqualTo(", ");
        assertThat(mapSpec.suffix).isEqualTo(">>");
    }
}
