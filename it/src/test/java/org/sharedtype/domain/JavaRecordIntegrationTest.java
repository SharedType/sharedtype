package org.sharedtype.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.domain.TypeDefDeserializer.deserializeTypeDef;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JavaRecordIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("JavaRecord.ser");

    @Test
    void duplicateAccessorField() {
        var duplicateAccessorField = classDef.components().get(0);
        assertThat(duplicateAccessorField.name()).isEqualTo("duplicateAccessor");
        var typeInfo = (ConcreteTypeInfo)duplicateAccessorField.type();
        assertThat(typeInfo.simpleName()).isEqualTo("string");
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void stringField() {
        var stringField = classDef.components().get(1);
        assertThat(stringField.name()).isEqualTo("string");
        var typeInfo = (ConcreteTypeInfo)stringField.type();
        assertThat(typeInfo.simpleName()).isEqualTo("string");
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }
}
