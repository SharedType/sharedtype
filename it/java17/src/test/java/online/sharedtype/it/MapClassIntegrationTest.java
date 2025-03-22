package online.sharedtype.it;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
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
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)mapField.type();
                assertThat(typeInfo.getKind()).isEqualTo(ConcreteTypeInfo.Kind.MAP);
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.concurrent.ConcurrentMap");
                assertThat(typeInfo.typeArgs()).hasSize(2).satisfiesExactly(
                    keyType -> assertThat(keyType).isEqualTo(Constants.BOXED_INT_TYPE_INFO),
                    valueType -> assertThat(valueType).isEqualTo(Constants.STRING_TYPE_INFO)
                );
            },
            enumKeyMapField -> {
                assertThat(enumKeyMapField.name()).isEqualTo("enumKeyMapField");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo) enumKeyMapField.type();
                assertThat(typeInfo.getKind()).isEqualTo(ConcreteTypeInfo.Kind.MAP);
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Map");
                assertThat(typeInfo.typeArgs()).hasSize(2).satisfiesExactly(
                    keyType -> {
                        ConcreteTypeInfo keyTypeInfo = (ConcreteTypeInfo) keyType;
                        assertThat(keyTypeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumSize");
                    },
                    valueType -> assertThat(valueType).isEqualTo(Constants.STRING_TYPE_INFO)
                );
            },
            customMapField -> {
                assertThat(customMapField.name()).isEqualTo("customMapField");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo) customMapField.type();
                assertThat(typeInfo.getKind()).isEqualTo(ConcreteTypeInfo.Kind.MAP);
                assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.CustomMap");
                assertThat(typeInfo.typeArgs()).hasSize(0);
            },
            nestedMapField -> {
                assertThat(nestedMapField.name()).isEqualTo("nestedMapField");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo) nestedMapField.type();
                assertThat(typeInfo.getKind()).isEqualTo(ConcreteTypeInfo.Kind.MAP);
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.util.Map");
                assertThat(typeInfo.typeArgs()).hasSize(2).satisfiesExactly(
                    keyType -> assertThat(keyType).isEqualTo(Constants.STRING_TYPE_INFO),
                    valueType -> {
                        ConcreteTypeInfo valueTypeInfo = (ConcreteTypeInfo) valueType;
                        assertThat(typeInfo.getKind()).isEqualTo(ConcreteTypeInfo.Kind.MAP);
                        assertThat(valueTypeInfo.qualifiedName()).isEqualTo("java.util.Map");
                        assertThat(valueTypeInfo.typeArgs()).hasSize(2).satisfiesExactly(
                            nestedKeyType -> assertThat(nestedKeyType).isEqualTo(Constants.STRING_TYPE_INFO),
                            nestedValueType -> assertThat(nestedValueType).isEqualTo(Constants.BOXED_INT_TYPE_INFO)
                        );
                    }
                );
            }
        );
    }
}
