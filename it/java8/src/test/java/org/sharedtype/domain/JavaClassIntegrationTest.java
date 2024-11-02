package org.sharedtype.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.domain.TypeDefDeserializer.deserializeTypeDef;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JavaClassIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("JavaClass.ser");

    @Test
    void parseFields() {
        assertThat(classDef.components()).satisfiesExactly(
            string -> {
                assertThat(string.name()).isEqualTo("string");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)string.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            },
            size -> {
                assertThat(size.name()).isEqualTo("size");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)size.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.types.EnumSize");
            }
        );
    }
}
