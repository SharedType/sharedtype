package online.sharedtype.it;

import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class EnumIntegrationTest {
    @Test
    void parseEnumTShirt() {
        EnumDef enumDef = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumTShirt.ser");
        assertThat(enumDef.simpleName()).isEqualTo("EnumTShirt");
        assertThat(enumDef.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumTShirt");
        assertThat(enumDef.components()).satisfiesExactly(
            c1 -> {
                assertThat(c1.value().getValue()).isEqualTo("S");
                assertThat(c1.value().getEnumConstantName()).isEqualTo("S");
            },
            c2 -> assertThat(c2.value().getValue()).isEqualTo("M"),
            c3 -> assertThat(c3.value().getValue()).isEqualTo("L")
        );
    }

    @Test
    void parseEnumSize() {
        EnumDef enumSize = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumSize.ser");
        assertThat(enumSize.simpleName()).isEqualTo("EnumSize");
        assertThat(enumSize.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumSize");
        assertThat(enumSize.components()).hasSize(3).allMatch(constant -> {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("int");
        });

        EnumValueInfo constant1 = enumSize.components().get(0);
        assertThat(constant1.value().getValue()).isEqualTo(1);
        assertThat(constant1.value().getEnumConstantName()).isEqualTo("SMALL");

        EnumValueInfo constant2 = enumSize.components().get(1);
        assertThat(constant2.value().getValue()).isEqualTo(2);

        EnumValueInfo constant3 = enumSize.components().get(2);
        assertThat(constant3.value().getValue()).isEqualTo(3);
    }

    @Test
    void enumGalaxy() {
        EnumDef enumGalaxy = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumGalaxy.ser");
        assertThat(enumGalaxy.simpleName()).isEqualTo("EnumGalaxy");
        assertThat(enumGalaxy.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumGalaxy");
        assertThat(enumGalaxy.components()).hasSize(3).allMatch(constant -> {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("online.sharedtype.it.java8.EnumGalaxy");
        });
        EnumValueInfo constant1 = enumGalaxy.components().get(0);
        assertThat(constant1.value().getValue()).isEqualTo("MilkyWay");
        assertThat(constant1.value().getEnumConstantName()).isEqualTo("MilkyWay");
        var constant1EnumType = (ConcreteTypeInfo)constant1.value().getValueType();
        assertThat(constant1EnumType.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumGalaxy");

        EnumValueInfo constant2 = enumGalaxy.components().get(1);
        assertThat(constant2.value().getValue()).isEqualTo("Andromeda");

        EnumValueInfo constant3 = enumGalaxy.components().get(2);
        assertThat(constant3.value().getValue()).isEqualTo("Triangulum");
    }

    @Test
    void parseEnumConstReference() {
        EnumDef enumDef = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumConstReference.ser");
        assertThat(enumDef.simpleName()).isEqualTo("EnumConstReference");
        assertThat(enumDef.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumConstReference");
        assertThat(enumDef.components()).hasSize(2);

        EnumValueInfo constant1 = enumDef.components().get(0);
        assertThat(constant1.value().getValue()).isEqualTo(999L);
        assertThat(constant1.value().getEnumConstantName()).isEqualTo("ReferenceConstantInOther");
        var constant1TypeInfo = (ConcreteTypeInfo)constant1.value().getValueType();
        assertThat(constant1.type()).isEqualTo(constant1TypeInfo);
        assertThat(constant1TypeInfo.qualifiedName()).isEqualTo("long");

        EnumValueInfo constant2 = enumDef.components().get(1);
        assertThat(constant2.value().getValue()).isEqualTo(156L);
        assertThat(constant2.value().getEnumConstantName()).isEqualTo("ReferenceConstantLocally");
        var constant2TypeInfo = (ConcreteTypeInfo)constant2.value().getValueType();
        assertThat(constant2.type()).isEqualTo(constant2TypeInfo);
        assertThat(constant2TypeInfo.qualifiedName()).isEqualTo("long");
    }

    @Test
    void parseEnumEnumReference() {
        EnumDef enumDef = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumEnumReference.ser");
        assertThat(enumDef.simpleName()).isEqualTo("EnumEnumReference");
        assertThat(enumDef.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumEnumReference");
        assertThat(enumDef.components()).hasSize(1);

        EnumValueInfo constant1 = enumDef.components().get(0);
        assertThat(constant1.value().getValue()).isEqualTo(3);
        assertThat(constant1.value().getEnumConstantName()).isEqualTo("ReferenceAnother");
        var constant1TypeInfo = (ConcreteTypeInfo)constant1.value().getValueType();
        assertThat(constant1.type()).isEqualTo(constant1TypeInfo);
        assertThat(constant1TypeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumSize"); // TODO: should be int? EnumSize's value type is effectively int
    }
}
