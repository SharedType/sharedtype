package org.sharedtype.it;

import org.junit.jupiter.api.Test;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.it.TypeDefDeserializer.deserializeTypeDef;

final class AnotherJavaClassIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("org.sharedtype.it.java8.other.JavaClass.ser");

    @Test
    void parseFields() {
        assertThat(classDef.components()).satisfiesExactly(
            value -> {
                assertThat(value.name()).isEqualTo("value");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)value.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("int");
            }
        );
    }
}
