package online.sharedtype.processor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ClassDefTest {
    @Test
    void reifySupertypes() {
        // class Base<T> implements A<T>, B<T> {}
        ClassDef classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Base").simpleName("Base")
            .supertypes(List.of(
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.A")
                    .resolved(true)
                    .typeArgs(List.of(
                        TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build()
                    )).build(),
                ConcreteTypeInfo.builder()
                    .qualifiedName("com.github.cuzfrog.B")
                    .resolved(true)
                    .typeArgs(List.of(
                        new ArrayTypeInfo(
                            TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build()
                        )
                    )).build()
            ))
            .typeVariables(List.of(
                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build()
            ))
            .build();

        var reifiedSuperTypes = classDef.reify(List.of(Constants.BOXED_INT_TYPE_INFO)).directSupertypes();
        var reifiedA = (ConcreteTypeInfo) reifiedSuperTypes.get(0);
        assertThat(reifiedA.typeArgs()).satisfiesExactly(arg -> assertThat(arg).isEqualTo(Constants.BOXED_INT_TYPE_INFO));

        var reifiedB = (ConcreteTypeInfo) reifiedSuperTypes.get(1);
        assertThat(reifiedB.typeArgs()).satisfiesExactly(arg -> assertThat(arg).isEqualTo(new ArrayTypeInfo(Constants.BOXED_INT_TYPE_INFO)));
    }

    @Test
    void reifyComponents() {
        /* class Base<T, K> {
          T field1,
          List<Tuple<T, K> field2
        } */
        ClassDef classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Base").simpleName("Base")
            .components(List.of(
                FieldComponentInfo.builder().name("field1")
                    .type(TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build())
                    .build(),
                FieldComponentInfo.builder().name("field2")
                    .type(new ArrayTypeInfo(
                        ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.Tuple").simpleName("Container")
                            .typeArgs(List.of(
                                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build(),
                                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("K").build()
                            ))
                            .build()
                    ))
                    .build()
            ))
            .typeVariables(List.of(
                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build(),
                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("K").build()
            ))
            .build();
        var reifiedClassDef = classDef.reify(List.of(Constants.STRING_TYPE_INFO, Constants.BOXED_INT_TYPE_INFO));
        assertThat(reifiedClassDef.components().get(0).type()).isEqualTo(Constants.STRING_TYPE_INFO);
        assertThat(reifiedClassDef.components().get(1).type()).isEqualTo(new ArrayTypeInfo(
                        ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.Tuple").simpleName("Container")
                            .typeArgs(List.of(Constants.STRING_TYPE_INFO, Constants.BOXED_INT_TYPE_INFO))
                            .build()
                    ));
    }

    @Test
    void illegalArgsNumbers() {
        ClassDef classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Base").simpleName("Base")
            .typeVariables(List.of(
                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("T").build(),
                TypeVariableInfo.builder().contextTypeQualifiedName("com.github.cuzfrog.Base").name("K").build()
            ))
            .build();
        assertThatThrownBy(() -> classDef.reify(List.of()));
    }

    @Test
    void returnSelfIfNoTypeArgs() {
        ClassDef classDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Base").simpleName("Base")
            .build();
        assertThat(classDef.reify(List.of())).isSameAs(classDef);
    }
}
