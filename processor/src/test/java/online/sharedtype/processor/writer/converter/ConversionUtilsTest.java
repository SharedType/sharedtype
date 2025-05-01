package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @Test
    void buildRustMacroTraitsExpr() {
        assertThat(ConversionUtils.buildRustMacroTraitsExpr(Set.of())).isNull();
        var macros = new LinkedHashSet<String>(2);
        macros.add("A");
        macros.add("B");
        assertThat(ConversionUtils.buildRustMacroTraitsExpr(macros)).isEqualTo("#[derive(A, B)]");
    }
}
