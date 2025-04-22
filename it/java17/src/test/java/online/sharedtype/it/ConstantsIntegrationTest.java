package online.sharedtype.it;

import online.sharedtype.processor.domain.ConstantNamespaceDef;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class ConstantsIntegrationTest {
    @Test
    void parseMyConstants() {
        ConstantNamespaceDef constantsDef = (ConstantNamespaceDef) deserializeTypeDef("$online.sharedtype.it.java8.MyConstants.ser");
        assertThat(constantsDef.simpleName()).isEqualTo("MyConstants");
        var components = constantsDef.components();
        assertThat(components).hasSize(22);
        assertThat(components).satisfiesExactly(
            component -> {
                assertThat(component.name()).isEqualTo("FLOAT_VALUE");
                assertThat(component.value()).isEqualTo(1.888f);
            },
            component -> {
                assertThat(component.name()).isEqualTo("LONG_VALUE");
                assertThat(component.value()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_LOCAL_VALUE");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_IMPORTED_VALUE");
                assertThat(component.value()).isEqualTo(666L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_NESTED_VALUE");
                assertThat(component.value()).isEqualTo(777L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_IMPORTED_VALUE");
                assertThat(component.value()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("DOUBLE_REFERENCED_VALUE");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_PACKAGE_PRIVATE_VALUE");
                assertThat(component.value()).isEqualTo(123L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_SUPER_VALUE");
                assertThat(component.value()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELECTED_SUPER_VALUE");
                assertThat(component.value()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(112L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELF_REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_IMPORTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(666L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_NESTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(777L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_IMPORTED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(999L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("DOUBLE_REFERENCED_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(555L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_PACKAGE_PRIVATE_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(123L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_SUPER_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("SELECTED_SUPER_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(345L);
            },
            component -> {
                assertThat(component.name()).isEqualTo("REFERENCED_STATIC_VALUE_IN_STATIC_BLOCK");
                assertThat(component.value()).isEqualTo(787L);
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
                assertThat(component.value()).isEqualTo(345L);
            }
        );
    }
}
