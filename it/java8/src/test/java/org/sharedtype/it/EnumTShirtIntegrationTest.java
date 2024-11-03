package org.sharedtype.it;

import org.junit.jupiter.api.Test;
import org.sharedtype.domain.EnumDef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.it.TypeDefDeserializer.deserializeTypeDef;

final class EnumTShirtIntegrationTest {
    private final EnumDef enumDef = (EnumDef) deserializeTypeDef("EnumTShirt.ser");

    @Test
    void parseEnumTShirt() {
        assertThat(enumDef.simpleName()).isEqualTo("EnumTShirt");
        assertThat(enumDef.qualifiedName()).isEqualTo("org.sharedtype.it.java8.EnumTShirt");
        assertThat(enumDef.components()).satisfiesExactly(
            c1 -> assertThat(c1.value()).isEqualTo("S"),
            c2 -> assertThat(c2.value()).isEqualTo("M"),
            c3 -> assertThat(c3.value()).isEqualTo("L")
        );
    }
}
