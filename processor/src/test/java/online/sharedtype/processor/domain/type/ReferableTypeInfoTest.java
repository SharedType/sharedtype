package online.sharedtype.processor.domain.type;

import online.sharedtype.processor.domain.def.ClassDef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ReferableTypeInfoTest {
    @Test
    void arrayNestedReferencedType() {
        var concreteTypeInfo = ConcreteTypeInfo.builder().build();
        var wrapped = new ArrayTypeInfo(concreteTypeInfo);

        var typeDef = ClassDef.builder().build();
        wrapped.addReferencingType(typeDef);

        assertThat(concreteTypeInfo.referencingTypes()).containsExactly(typeDef);
    }
}
