package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ConversionUtilsTest {
    @Test
    void toSnakeCase() {
        assertThat(ConversionUtils.toSnakeCase("camelCase")).isEqualTo("camel_case");
        assertThat(ConversionUtils.toSnakeCase("CamelCase")).isEqualTo("camel_case");
        assertThat(ConversionUtils.toSnakeCase("camelcase")).isEqualTo("camelcase");
        assertThat(ConversionUtils.toSnakeCase("CAMELCASE")).isEqualTo("camelcase");
        assertThat(ConversionUtils.toSnakeCase("camelCase123")).isEqualTo("camel_case123");
        assertThat(ConversionUtils.toSnakeCase("CamelCase123")).isEqualTo("camel_case123");
        assertThat(ConversionUtils.toSnakeCase("camelcase123")).isEqualTo("camelcase123");
        assertThat(ConversionUtils.toSnakeCase("CAMELCASE123")).isEqualTo("camelcase123");
    }

    @Test
    void optionalField() {
        var field = FieldComponentInfo.builder().build();
        assertThat(ConversionUtils.isOptionalField(field)).isFalse();
        field.setOptional(true);
        assertThat(ConversionUtils.isOptionalField(field)).isTrue();
    }

    @Test
    void cyclicReferencedTypeFieldIsOptional() {
        var typeDef = ClassDef.builder().build();
        var typeInfo = ConcreteTypeInfo.builder().typeDef(typeDef).build();
        var field = FieldComponentInfo.builder().type(typeInfo).build();
        assertThat(ConversionUtils.isOptionalField(field)).isFalse();

        typeDef.setCyclicReferenced(true);
        assertThat(ConversionUtils.isOptionalField(field)).isTrue();
    }
}
