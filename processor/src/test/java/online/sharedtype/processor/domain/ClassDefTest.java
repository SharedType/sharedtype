package online.sharedtype.processor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ClassDefTest {
    @Test
    void allSupertypes() {
        ClassDef classDef = ClassDef.builder()
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.A")
                    .resolved(true)
                    .typeDef(ClassDef.builder().qualifiedName("com.github.cuzfrog.A").simpleName("A").supertypes(List.of(
                        ConcreteTypeInfo.builder().resolved(true).qualifiedName("com.github.cuzfrog.AA").simpleName("AA").build()
                    )).build())
                    .build(),
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.B")
                    .resolved(true)
                    .build()
            ))
            .build();

        var supertypes = classDef.allSupertypes();
        assertThat(supertypes).hasSize(3);
        ConcreteTypeInfo supertypeInfo1 = (ConcreteTypeInfo)supertypes.get(0);
        assertThat(supertypeInfo1.qualifiedName()).isEqualTo("com.github.cuzfrog.A");
        ConcreteTypeInfo supertypeInfo2 = (ConcreteTypeInfo)supertypes.get(1);
        assertThat(supertypeInfo2.qualifiedName()).isEqualTo("com.github.cuzfrog.B");
        ConcreteTypeInfo supertypeInfo3 = (ConcreteTypeInfo)supertypes.get(2);
        assertThat(supertypeInfo3.qualifiedName()).isEqualTo("com.github.cuzfrog.AA");
    }

    @Test
    void errorCallingAllSupertypesWhenSupertypeIsNotResolved() {
        ClassDef classDef = ClassDef.builder()
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.B")
                    .resolved(false)
                    .build()
            ))
            .build();
        assertThatThrownBy(classDef::allSupertypes);
    }
}
