package online.sharedtype.processor.resolver;


import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
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
        var referencedTypeT3 = ConcreteTypeInfo.builder().qualifiedName("a.b.A")
            .typeArgs(List.of(TypeVariableInfo.builder().name("T3").build())).build();
        referencedTypeT3.addReferencingType(classDefReferencing1);
        classDef.linkTypeInfo(referencedTypeT3);

        var classDefReferencing2 = ClassDef.builder().qualifiedName("a.b.R2").annotated(true)
            .build();
        var referencedTypeC4 = ConcreteTypeInfo.builder().qualifiedName("a.b.C4").build();
        referencedTypeC4.addReferencingType(classDefReferencing2);
        classDefReferencing1.linkTypeInfo(referencedTypeC4);

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
        var typeInfoA = ConcreteTypeInfo.builder().qualifiedName("a.b.A").build();
        typeInfoA.addReferencingType(classDefC);
        var typeInfoB = ConcreteTypeInfo.builder().qualifiedName("a.b.B").build();
        typeInfoB.addReferencingType(classDefA);
        var typeInfoC = ConcreteTypeInfo.builder().qualifiedName("a.b.C").build();
        typeInfoC.addReferencingType(classDefB);
        classDefA.linkTypeInfo(typeInfoA);
        classDefB.linkTypeInfo(typeInfoB);
        classDefC.linkTypeInfo(typeInfoC);

        referenceResolver.resolve(List.of(classDefA, classDefB, classDefC));
        assertThat(classDefA.isCyclicReferenced()).isTrue();
        assertThat(classDefB.isCyclicReferenced()).isTrue();
        assertThat(classDefC.isCyclicReferenced()).isTrue();
    }
}
