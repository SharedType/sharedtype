package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class RustStructConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustStructConverter converter = new RustStructConverter(TypeExpressionConverter.rust(ctxMocks.getContext()));

    @Test
    void skipNonClassDef() {
        assertThat(converter.shouldAccept(EnumDef.builder().build())).isFalse();
    }

    @Test
    void shouldAcceptClassDefAnnotated() {
        assertThat(converter.shouldAccept(ClassDef.builder().build())).isFalse();
        assertThat(converter.shouldAccept(ClassDef.builder().annotated(true).build())).isTrue();
        assertThat(converter.shouldAccept(ClassDef.builder().referencedByAnnotated(true).build())).isTrue();
    }


    @Test
    void convert() {
    }
}
