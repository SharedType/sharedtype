package online.sharedtype.it;

import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.def.ConstantNamespaceDef;
import online.sharedtype.processor.domain.value.EnumConstantValue;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class ConstantsIntegrationTest {
    @Test
    void parseMyConstants() {
        ConstantNamespaceDef constantsDef = (ConstantNamespaceDef) deserializeTypeDef("$online.sharedtype.it.java8.MyConstants.ser");
        assertThat(constantsDef.simpleName()).isEqualTo("MyConstants");
        var components = constantsDef.components();
        assertThat(components).hasSize(33);
        assertThat(components).satisfiesExactly(
            component -> {
                assertThat(component.name()).isEqualTo("FLOAT_VALUE");
                assertThat(component.value().getValue()).isEqualTo(1.888f);
            },
            component -> {
                assertThat(component.name()).isEqualTo("LONG_VALUE");
                assertThat(component.value().getValue()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_LOCAL_VALUE");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE_QUALIFIED_NAME");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_IMPORTED_VALUE");
                assertThat(component.value().getValue()).isEqualTo(666L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_NESTED_VALUE");
                assertThat(component.value().getValue()).isEqualTo(777L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_IMPORTED_VALUE");
                assertThat(component.value().getValue()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("DOUBLE_REFERENCED_VALUE");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_PACKAGE_PRIVATE_VALUE");
                assertThat(component.value().getValue()).isEqualTo(123L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_SUPER_VALUE");
                assertThat(component.value().getValue()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELECTED_SUPER_VALUE");
                assertThat(component.value().getValue()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE");
                EnumConstantValue value = (EnumConstantValue) component.value();
                assertThat(value.getEnumConstantName()).isEqualTo("MilkyWay");
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE2");
                assertThat(component.value().getValue()).isEqualTo("S");
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE3");
                assertThat(component.value().getValue()).isEqualTo(1);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(112L);
                assertThat(component.getTagLiterals(SharedType.TargetType.GO)).anyMatch(s -> s.getContents().contains("// test comments for inlined constant"));
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE_QUALIFIED_NAME_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_IMPORTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(666L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_NESTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(777L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_IMPORTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("DOUBLE_REFERENCED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_PACKAGE_PRIVATE_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(123L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_SUPER_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELECTED_SUPER_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(787L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE_IN_STATIC_BLOCK");
                EnumConstantValue value = (EnumConstantValue) component.value();
                assertThat(value.getEnumConstantName()).isEqualTo("MilkyWay");
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE2_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo("S");
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_ENUM_VALUE3_IN_STATIC_BLOCK");
                assertThat(component.value().getValue()).isEqualTo(1);
            },
            component -> {
                assertThat(component.name()).isEqualTo("MATH_VALUE");
                assertThat(component.value().getValue()).isEqualTo("1.1");
            },
            component -> {
                assertThat(component.name()).isEqualTo("MATH_VALUE_QUALIFIED_NAME");
                assertThat(component.value().getValue()).isEqualTo("88885555");
            },
            component -> {
                assertThat(component.name()).isEqualTo("MATH_VALUE_REFERENCED_LOCAL_VALUE");
                assertThat(component.value().getValue()).isEqualTo("555");
            }
        );
    }

    @Test
    void parseMyConstantsInner() {
        ConstantNamespaceDef constantsDef = (ConstantNamespaceDef) deserializeTypeDef("$online.sharedtype.it.java8.MyConstants.InnerConstantClass.ser");
        assertThat(constantsDef.simpleName()).isEqualTo("InnerConstantClass");
        var components = constantsDef.components();
        assertThat(components).satisfiesExactly(
            component -> {
                assertThat(component.name()).isEqualTo("INNER_REFERENCED_SUPER_VALUE_IN_STATIC");
                assertThat(component.value().getValue()).isEqualTo(345L);
            }
        );
    }
}
