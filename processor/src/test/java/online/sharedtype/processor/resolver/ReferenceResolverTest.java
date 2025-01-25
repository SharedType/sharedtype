package online.sharedtype.processor.resolver;


import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

final class ReferenceResolverTest {
    private final ReferenceResolver referenceResolver = new ReferenceResolver();

    @Test
    void markReferencedByAnnotated() {
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
            .referencingTypes(Set.of(classDefReferencing1)).build());

        var classDefReferencing2 = ClassDef.builder().qualifiedName("a.b.R2").annotated(true)
            .build();
        classDefReferencing1.linkTypeInfo(ConcreteTypeInfo.builder()
            .qualifiedName("a.b.C4").referencingTypes(Set.of(classDefReferencing2)).build());

        referenceResolver.resolve(List.of(classDef));
        assertThat(classDef.isReferencedByAnnotated()).isTrue();
    }

    @Test
    void markCyclicReferenced() {
        var classDefA = ClassDef.builder().qualifiedName("a.b.A")
            .build();
        var classDefB = ClassDef.builder().qualifiedName("a.b.B")
            .build();
        var classDefC = ClassDef.builder().qualifiedName("a.b.C")
            .build();
        classDefA.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.A").referencingTypes(Set.of(classDefC)).build());
        classDefB.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.B").referencingTypes(Set.of(classDefA)).build());
        classDefC.linkTypeInfo(ConcreteTypeInfo.builder().qualifiedName("a.b.C").referencingTypes(Set.of(classDefB)).build());

        referenceResolver.resolve(List.of(classDefA, classDefB, classDefC));
        assertThat(classDefA.isCyclicReferenced()).isTrue();
        assertThat(classDefB.isCyclicReferenced()).isTrue();
        assertThat(classDefC.isCyclicReferenced()).isTrue();
    }
}
