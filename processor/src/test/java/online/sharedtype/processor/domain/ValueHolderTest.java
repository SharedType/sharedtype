package online.sharedtype.processor.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ValueHolderTest {

    @Test
    void literalValue() {
        assertThat(ValueHolder.of("abc").literalValue()).isEqualTo("\"abc\"");
        assertThat(ValueHolder.of(105).literalValue()).isEqualTo("105");
    }

    @Test
    void getNestedValue() {
        var value = ValueHolder.of(ValueHolder.of(
            ValueHolder.ofEnum(null, "ENUM_CONST", null, ValueHolder.of(ValueHolder.of("Value1")))));
        assertThat(value.value()).isEqualTo("Value1");
    }
}
