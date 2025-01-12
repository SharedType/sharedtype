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
    private final RustStructConverter converter = new RustStructConverter(ctxMocks.getContext(), TypeExpressionConverter.rust(ctxMocks.getContext()));

    @Test
    void skipNonClassDef() {
        assertThat(converter.shouldAccept(EnumDef.builder().build())).isFalse();
    }

    @Test
    void shouldAcceptClassDefAnnotated() {
        assertThat(converter.shouldAccept(ClassDef.builder().annotated(true).build())).isTrue();
    }

    @Test
    void shouldAcceptClassDefReferencedByAnnotated() {
        var classDef = ClassDef.builder().qualifiedName("a.b.A")
            .build();
        var classDefReferencing1 = ClassDef.builder().qualifiedName("a.b.R1")
            .build();
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.A")
            .typeArgs(List.of(TypeVariableInfo.builder().name("T1").build())).build());
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.A")
            .typeArgs(List.of(TypeVariableInfo.builder().name("T2").build())).build());
        classDef.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.A")
            .typeArgs(List.of(TypeVariableInfo.builder().name("T3").build()))
            .referencingTypes(Set.of(classDefReferencing1.qualifiedName())).build());

        var classDefReferencing2 = ClassDef.builder().qualifiedName("a.b.R2").annotated(true)
            .build();
        classDefReferencing1.linkTypeInfo(ConcreteTypeInfo.builder()
            .qualifiedName("a.b.C4").referencingTypes(Set.of(classDefReferencing2.qualifiedName())).build());

        var mockTypeStore = ctxMocks.getTypeStore();
        when(mockTypeStore.getTypeDef("a.b.R1")).thenReturn(classDefReferencing1);
        when(mockTypeStore.getTypeDef("a.b.R2")).thenReturn(classDefReferencing2);

        assertThat(converter.shouldAccept(classDef)).isTrue();
    }

    @Test
    void shouldNotEnterInfiniteLoopForCyclicReferencedTypes() {
        var classDefA = ClassDef.builder().qualifiedName("a.b.A")
            .build();
        var classDefB = ClassDef.builder().qualifiedName("a.b.B")
            .build();
        var classDefC = ClassDef.builder().qualifiedName("a.b.C")
            .build();
        classDefA.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.A").referencingTypes(Set.of(classDefC.qualifiedName())).build());
        classDefB.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.B").referencingTypes(Set.of(classDefA.qualifiedName())).build());
        classDefC.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.C").referencingTypes(Set.of(classDefB.qualifiedName())).build());
        var mockTypeStore = ctxMocks.getTypeStore();
        when(mockTypeStore.getTypeDef("a.b.A")).thenReturn(classDefA);
        when(mockTypeStore.getTypeDef("a.b.B")).thenReturn(classDefB);
        when(mockTypeStore.getTypeDef("a.b.C")).thenReturn(classDefC);

        Awaitility.waitAtMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(converter.shouldAccept(classDefA)).isFalse();
        });
    }

    @Test
    void convert() {
    }
}
