package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.ContextMocks;
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
}
