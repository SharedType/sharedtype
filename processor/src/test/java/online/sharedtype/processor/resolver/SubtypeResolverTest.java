package online.sharedtype.processor.resolver;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class SubtypeResolverTest {
    private final SubtypeResolver resolver = new SubtypeResolver();

    @Test
    void resolve() {
        ClassDef superTypeDef1 = ClassDef.builder().simpleName("SuperClassA").build();
        ClassDef superTypeDef2 = ClassDef.builder().simpleName("SuperClassB").build();

        ConcreteTypeInfo superType1 = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.SuperClassA")
            .simpleName("SuperClassA")
            .typeDef(superTypeDef1)
            .build();
        ConcreteTypeInfo superType2 = ConcreteTypeInfo.builder()
            .qualifiedName("com.github.cuzfrog.SuperClassB")
            .simpleName("SuperClassB")
            .typeDef(superTypeDef2)
            .build();
        ClassDef classDef1 = ClassDef.builder()
            .simpleName("ClassA")
            .qualifiedName("com.github.cuzfrog.ClassA")
            .supertypes(List.of(superType1, superType2))
            .build();
        ClassDef classDef2 = ClassDef.builder()
            .simpleName("ClassB")
            .qualifiedName("com.github.cuzfrog.ClassB")
            .supertypes(List.of(superType1))
            .build();

        List<TypeDef> input = List.of(classDef1, classDef2);
        var res = resolver.resolve(input);
        assertThat(res).isEqualTo(input);
        assertThat(superTypeDef1.directSubtypes()).containsExactlyInAnyOrder(classDef1, classDef2);
        assertThat(superTypeDef2.directSubtypes()).containsExactly(classDef1);
    }
}
