package online.sharedtype.processor.domain.value;

import online.sharedtype.processor.domain.Constants;
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
            ValueHolder.ofEnum("ENUM_CONST", Constants.STRING_TYPE_INFO, ValueHolder.of(ValueHolder.of("Value1")))));
        assertThat(value.getValue()).isEqualTo("Value1");
    }

    @Test
    void equalities() {
        assertThat(ValueHolder.of("abc")).isEqualTo(ValueHolder.of(ValueHolder.of("abc")));
    }
}
