package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.writer.converter.type.TypeExpressionConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class RustStructConverterTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final RustStructConverter converter = new RustStructConverter(ctxMocks.getContext(), TypeExpressionConverter.rust());

    @Test
    void skipNonClassDef() {
        assertThat(converter.supports(EnumDef.builder().build())).isFalse();
    }

    @Test
    void shouldSupportClassDefAnnotated() {
        assertThat(converter.supports(ClassDef.builder().annotated(true).build())).isTrue();
    }

    @Test
    void shouldSupportClassDefReferencedByAnnotated() {
        var classDef = ClassDef.builder().qualifiedName("a.b.A")
            .build();
        var classDefReferencing1 = ClassDef.builder().qualifiedName("a.b.R1")
            .build();
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.C1").build());
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.C2").build());
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.C3").referencingTypes(Set.of(classDefReferencing1.qualifiedName())).build());

        var classDefReferencing2 = ClassDef.builder().qualifiedName("a.b.R2").annotated(true)
            .build();
        classDefReferencing1.linkTypeInfo(ConcreteTypeInfo.builder()
            .qualifiedName("a.b.C4").referencingTypes(Set.of(classDefReferencing2.qualifiedName())).build());

        var mockTypeStore = ctxMocks.getTypeStore();
        when(mockTypeStore.getTypeDef("a.b.R1")).thenReturn(classDefReferencing1);
        when(mockTypeStore.getTypeDef("a.b.R2")).thenReturn(classDefReferencing2);

        assertThat(converter.supports(classDef)).isTrue();
    }

    @Test
    void convert() {
    }
}
