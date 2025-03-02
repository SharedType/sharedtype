package online.sharedtype.it;

import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
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
            c1 -> assertThat(c1.value()).isEqualTo("S"),
            c2 -> assertThat(c2.value()).isEqualTo("M"),
            c3 -> assertThat(c3.value()).isEqualTo("L")
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
        assertThat(constant1.value()).isEqualTo(1);

        EnumValueInfo constant2 = enumSize.components().get(1);
        assertThat(constant2.value()).isEqualTo(2);

        EnumValueInfo constant3 = enumSize.components().get(2);
        assertThat(constant3.value()).isEqualTo(3);
    }
}
