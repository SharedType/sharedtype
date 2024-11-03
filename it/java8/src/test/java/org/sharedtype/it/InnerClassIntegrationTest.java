package org.sharedtype.it;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.it.TypeDefDeserializer.deserializeTypeDef;

final class InnerClassIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("InnerClass.ser");

    @Test
    void parseInnerClass() {
        assertThat(classDef.simpleName()).isEqualTo("InnerClass");
        assertThat(classDef.qualifiedName()).isEqualTo("org.sharedtype.it.java8.JavaClass.InnerClass");
        assertThat(classDef.components()).satisfiesExactly(
            value -> {
                assertThat(value.name()).isEqualTo("value");
                val fieldTypeInfo = (ConcreteTypeInfo)value.type();
                assertThat(fieldTypeInfo.qualifiedName()).isEqualTo("int");
            }
        );
    }
}
