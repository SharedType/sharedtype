package online.sharedtype.processor.writer.converter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class LiteralUtilsTest {

    @Test
    void literalValue() {
        assertThat(LiteralUtils.literalValue("abc")).isEqualTo("\"abc\"");
        assertThat(LiteralUtils.literalValue(105)).isEqualTo("105");
    }

    @Test
    void toSnakeCase() {
        assertThat(LiteralUtils.toSnakeCase("camelCase")).isEqualTo("camel_case");
        assertThat(LiteralUtils.toSnakeCase("CamelCase")).isEqualTo("camel_case");
        assertThat(LiteralUtils.toSnakeCase("camelcase")).isEqualTo("camelcase");
        assertThat(LiteralUtils.toSnakeCase("CAMELCASE")).isEqualTo("camelcase");
        assertThat(LiteralUtils.toSnakeCase("camelCase123")).isEqualTo("camel_case123");
        assertThat(LiteralUtils.toSnakeCase("CamelCase123")).isEqualTo("camel_case123");
        assertThat(LiteralUtils.toSnakeCase("camelcase123")).isEqualTo("camelcase123");
        assertThat(LiteralUtils.toSnakeCase("CAMELCASE123")).isEqualTo("camelcase123");
    }
}
