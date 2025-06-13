package online.sharedtype.it;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.MapTypeInfo;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class MapClassIntegrationTest {
    @Test
    void mapClass() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("online.sharedtype.it.java8.MapClass.ser");
        assertThat(classDef.components()).satisfiesExactly(
            mapField -> {
                assertThat(mapField.name()).isEqualTo("mapField");
                MapTypeInfo typeInfo = (MapTypeInfo)mapField.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.concurrent.ConcurrentMap");
                assertThat(typeInfo.keyType()).isEqualTo(Constants.BOXED_INT_TYPE_INFO);
                assertThat(typeInfo.valueType()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            enumKeyMapField -> {
                assertThat(enumKeyMapField.name()).isEqualTo("enumKeyMapField");
                MapTypeInfo typeInfo = (MapTypeInfo) enumKeyMapField.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Map");
                assertThat(typeInfo.keyType()).satisfies(
                    keyType -> {
                        ConcreteTypeInfo keyTypeInfo = (ConcreteTypeInfo) keyType;
                        assertThat(keyTypeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumSize");
                    }
                );
                assertThat(typeInfo.valueType()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            customMapField -> {
                assertThat(customMapField.name()).isEqualTo("customMapField");
                MapTypeInfo typeInfo = (MapTypeInfo) customMapField.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.CustomMap");
                assertThat(typeInfo.keyType()).isEqualTo(Constants.BOXED_INT_TYPE_INFO);
                assertThat(typeInfo.valueType()).isEqualTo(Constants.STRING_TYPE_INFO);
            },
            nestedMapField -> {
                assertThat(nestedMapField.name()).isEqualTo("nestedMapField");
                MapTypeInfo typeInfo = (MapTypeInfo) nestedMapField.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Map");
                assertThat(typeInfo.keyType()).isEqualTo(Constants.STRING_TYPE_INFO);
                assertThat(typeInfo.valueType()).satisfies(
                    valueType -> {
                        MapTypeInfo valueTypeInfo = (MapTypeInfo) valueType;
                        assertThat(valueTypeInfo.qualifiedName()).isEqualTo("java.util.Map");
                        assertThat(valueTypeInfo.keyType()).isEqualTo(Constants.STRING_TYPE_INFO);
                        assertThat(valueTypeInfo.valueType()).isEqualTo(Constants.BOXED_INT_TYPE_INFO);
                    }
                );
            }
        );
    }
}
