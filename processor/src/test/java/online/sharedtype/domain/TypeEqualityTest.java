package online.sharedtype.domain;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class TypeEqualityTest {
    @Test
    void differentTypeOfTypeInfoShouldNotEqual() {
        TypeInfo type1 = ConcreteTypeInfo.builder().qualifiedName("java.lang.String").build();
        TypeInfo arr = new ArrayTypeInfo(type1);
        assertThat(arr).isNotEqualTo(type1);
    }

    @Test
    void arrayWithSameComponentTypeShouldEqual() {
        TypeInfo type1 = ConcreteTypeInfo.builder().qualifiedName("java.lang.String").build();
        TypeInfo arr1 = new ArrayTypeInfo(type1);
        TypeInfo arr2 = new ArrayTypeInfo(type1);
        assertThat(arr1).isEqualTo(arr2);
    }

    @Test
    void genericTypeWithDifferentTypeArgumentsShouldNotEqual() {
        TypeInfo type1 = ConcreteTypeInfo.builder().qualifiedName("java.util.List").typeArgs(List.of(
            ConcreteTypeInfo.builder().qualifiedName("java.lang.String").build()
        )).build();
        TypeInfo type2 = ConcreteTypeInfo.builder().qualifiedName("java.util.List").typeArgs(List.of(
            ConcreteTypeInfo.builder().qualifiedName("java.lang.Integer").build()
        )).build();
        assertThat(type1).isNotEqualTo(type2);
    }
}
