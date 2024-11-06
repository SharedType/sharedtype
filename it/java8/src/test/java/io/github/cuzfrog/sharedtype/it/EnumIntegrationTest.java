package io.github.cuzfrog.sharedtype.it;

import io.github.cuzfrog.sharedtype.domain.ConcreteTypeInfo;
import io.github.cuzfrog.sharedtype.domain.EnumDef;
import io.github.cuzfrog.sharedtype.domain.EnumValueInfo;
import org.junit.jupiter.api.Test;

import static io.github.cuzfrog.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class EnumIntegrationTest {
    @Test
    void parseEnumTShirt() {
        EnumDef enumDef = (EnumDef) deserializeTypeDef("io.github.cuzfrog.sharedtype.it.java8.EnumTShirt.ser");
        assertThat(enumDef.simpleName()).isEqualTo("EnumTShirt");
        assertThat(enumDef.qualifiedName()).isEqualTo("io.github.cuzfrog.sharedtype.it.java8.EnumTShirt");
        assertThat(enumDef.components()).satisfiesExactly(
            c1 -> assertThat(c1.value()).isEqualTo("S"),
            c2 -> assertThat(c2.value()).isEqualTo("M"),
            c3 -> assertThat(c3.value()).isEqualTo("L")
        );
    }

    @Test
    void parseEnumSize() {
        EnumDef enumSize = (EnumDef) deserializeTypeDef("io.github.cuzfrog.sharedtype.it.java8.EnumSize.ser");
        assertThat(enumSize.simpleName()).isEqualTo("EnumSize");
        assertThat(enumSize.qualifiedName()).isEqualTo("io.github.cuzfrog.sharedtype.it.java8.EnumSize");
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
