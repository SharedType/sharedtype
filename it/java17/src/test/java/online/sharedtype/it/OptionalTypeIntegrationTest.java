package online.sharedtype.it;

import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.MapTypeInfo;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class OptionalTypeIntegrationTest {
    @Test
    void optionalMethodClass() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("online.sharedtype.it.java8.OptionalMethod.ser");
        assertThat(classDef.simpleName()).isEqualTo("OptionalMethod");
        assertThat(classDef.components()).satisfiesExactly(
            field1 -> {
                assertThat(field1.name()).isEqualTo("valueOptional");
                assertThat(field1.optional()).isTrue();
                assertThat(field1.type()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            field2 -> {
                assertThat(field2.name()).isEqualTo("nestedValueOptional");
                assertThat(field2.optional()).isTrue();
                assertThat(field2.type()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            field3 -> {
                assertThat(field3.name()).isEqualTo("setNestedValueOptional");
                assertThat(field3.optional()).isTrue();
                ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) field3.type();
                assertThat(arrayTypeInfo.component()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            field4 -> {
                assertThat(field4.name()).isEqualTo("mapNestedValueOptional");
                assertThat(field4.optional()).isTrue();
                MapTypeInfo fieldTypeInfo = (MapTypeInfo) field4.type();
                assertThat(fieldTypeInfo.qualifiedName()).isEqualTo("java.util.Map");
                assertThat(fieldTypeInfo.keyType()).isEqualTo(Constants.BOXED_INT_TYPE_INFO);
                assertThat(fieldTypeInfo.valueType()).isEqualTo(Constants.STRING_TYPE_INFO);
            }
        );
    }
}
