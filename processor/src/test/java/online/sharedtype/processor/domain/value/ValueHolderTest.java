package online.sharedtype.processor.domain.value;

import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import org.junit.jupiter.api.Test;

import static online.sharedtype.processor.domain.Constants.INT_TYPE_INFO;
import static online.sharedtype.processor.domain.Constants.STRING_TYPE_INFO;
import static org.assertj.core.api.Assertions.assertThat;

final class ValueHolderTest {

    @Test
    void literalValue() {
        assertThat(ValueHolder.of(STRING_TYPE_INFO, "abc").literalValue()).isEqualTo("\"abc\"");
        assertThat(ValueHolder.of(INT_TYPE_INFO, 105).literalValue()).isEqualTo("105");
    }

    @Test
    void getNestedValue() {
        var enumType = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").build();
        var value = ValueHolder.of(
            null,
            ValueHolder.of(
                enumType,
                ValueHolder.ofEnum(
                    "ENUM_CONST",
                    STRING_TYPE_INFO,
                    ValueHolder.of(STRING_TYPE_INFO, ValueHolder.of(STRING_TYPE_INFO, "Value1"))
                )
            )
        );
        assertThat(value.getValue()).isEqualTo("Value1");
    }

    @Test
    void equalities() {
        assertThat(ValueHolder.of(STRING_TYPE_INFO, "abc"))
            .isEqualTo(ValueHolder.of(STRING_TYPE_INFO, ValueHolder.of(STRING_TYPE_INFO,"abc")));
    }
}
