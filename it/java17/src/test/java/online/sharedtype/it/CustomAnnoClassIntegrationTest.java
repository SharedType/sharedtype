package online.sharedtype.it;

import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.EnumDef;
import org.junit.jupiter.api.Test;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class CustomAnnoClassIntegrationTest {
    @Test
    void customAnnoClass() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("online.sharedtype.it.java8.anno.CustomAnnoClass.ser");
        assertThat(classDef.simpleName()).isEqualTo("CustomAnnoClass");
        assertThat(classDef.components()).hasSize(1);
        var field1 = classDef.components().get(0);
        assertThat(field1.name()).isEqualTo("someValue");
    }

    @Test
    void customAnnoEnum() {
        EnumDef enumDef = (EnumDef)deserializeTypeDef("online.sharedtype.it.java8.anno.CustomAnnoEnum.ser");
        assertThat(enumDef.simpleName()).isEqualTo("CustomAnnoEnum");
        assertThat(enumDef.components()).hasSize(2);
        var field1 = enumDef.components().get(0);
        assertThat(field1.name()).isEqualTo("A");
        assertThat(field1.value()).isEqualTo(1);
        var field2 = enumDef.components().get(1);
        assertThat(field2.name()).isEqualTo("B");
        assertThat(field2.value()).isEqualTo(2);
    }
}
