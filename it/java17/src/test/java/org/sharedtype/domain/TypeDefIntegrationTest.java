package org.sharedtype.domain;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.domain.TypeDefDeserializer.deserializeTypeDef;

final class TypeDefIntegrationTest {
    @Test
    void container() {
        var container = (ClassDef) deserializeTypeDef("Container.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(container.simpleName()).isEqualTo("Container");
            softly.assertThat(container.qualifiedName()).isEqualTo("org.sharedtype.it.types.Container");
            softly.assertThat(container.components()).hasSize(1);
            var component1 = container.components().get(0);
            var field1Type = (TypeVariableInfo) component1.type();
            softly.assertThat(field1Type.getName()).isEqualTo("T");
            softly.assertThat(component1.name()).isEqualTo("t");

            softly.assertThat(container.resolved()).isTrue();
            softly.assertThat(container.typeVariables()).hasSize(1);
            var typeVariable1 = container.typeVariables().get(0);
            softly.assertThat(typeVariable1.getName()).isEqualTo("T");
        });
    }

    @Test
    void dependencyClassA() {
        var classA = (ClassDef) deserializeTypeDef("DependencyClassA.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(classA.simpleName()).isEqualTo("DependencyClassA");
            softly.assertThat(classA.qualifiedName()).isEqualTo("org.sharedtype.it.types.DependencyClassA");
            softly.assertThat(classA.components()).hasSize(1);

            var component1 = classA.components().get(0);
            softly.assertThat(component1.optional()).isFalse();
            softly.assertThat(component1.name()).isEqualTo("b");
            softly.assertThat(component1.type().resolved()).isTrue();
            var component1type = (ConcreteTypeInfo) component1.type();
            softly.assertThat(component1type.qualifiedName()).isEqualTo("org.sharedtype.it.types.DependencyClassB");

            softly.assertThat(classA.typeVariables()).isEmpty();
            softly.assertThat(classA.supertypes()).hasSize(1);
            var supertype1 = (ConcreteTypeInfo)classA.supertypes().get(0);
            softly.assertThat(supertype1.resolved()).isTrue();
            softly.assertThat(supertype1.qualifiedName()).isEqualTo("org.sharedtype.it.types.SuperClassA");
            softly.assertThat(classA.resolved()).isTrue();
        });
    }

    @Test
    void dependencyClassB() {
        var classB = (ClassDef) deserializeTypeDef("DependencyClassB.ser");
        assertThat(classB.simpleName()).isEqualTo("DependencyClassB");
    }

    @Test
    void dependencyClassC() {
        var classC = (ClassDef) deserializeTypeDef("DependencyClassC.ser");
        assertThat(classC.simpleName()).isEqualTo("DependencyClassC");
    }

    @Test
    void enumGalaxy() {
        var enumGalaxy = (EnumDef) deserializeTypeDef("EnumGalaxy.ser");
        assertThat(enumGalaxy.simpleName()).isEqualTo("EnumGalaxy");
        assertThat(enumGalaxy.qualifiedName()).isEqualTo("org.sharedtype.it.types.EnumGalaxy");
        assertThat(enumGalaxy.components()).hasSize(3).allMatch(constant -> {
            var typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("java.lang.String");
        });
        var constant1 = enumGalaxy.components().get(0);
        assertThat(constant1.value()).isEqualTo("MilkyWay");

        var constant2 = enumGalaxy.components().get(1);
        assertThat(constant2.value()).isEqualTo("Andromeda");

        var constant3 = enumGalaxy.components().get(2);
        assertThat(constant3.value()).isEqualTo("Triangulum");
    }

    @Test
    void enumSize() {
        var enumSize = (EnumDef) deserializeTypeDef("EnumSize.ser");
        assertThat(enumSize.simpleName()).isEqualTo("EnumSize");
        assertThat(enumSize.qualifiedName()).isEqualTo("org.sharedtype.it.types.EnumSize");
        assertThat(enumSize.components()).hasSize(3).allMatch(constant -> {
            var typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("int");
        });

        var constant1 = enumSize.components().get(0);
        assertThat(constant1.value()).isEqualTo(1);

        var constant2 = enumSize.components().get(1);
        assertThat(constant2.value()).isEqualTo(2);

        var constant3 = enumSize.components().get(2);
        assertThat(constant3.value()).isEqualTo(3);
    }

    @Test
    void interfaceA() {
        var interfaceA = (ClassDef) deserializeTypeDef("InterfaceA.ser");
        assertThat(interfaceA.simpleName()).isEqualTo("InterfaceA");
    }

    @Test
    void superClassA() {
        var superClassA = (ClassDef) deserializeTypeDef("SuperClassA.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(superClassA.simpleName()).isEqualTo("SuperClassA");
            softly.assertThat(superClassA.components()).hasSize(1);
            var component1 = superClassA.components().get(0);
            softly.assertThat(component1.name()).isEqualTo("a");
            var component1type = (ConcreteTypeInfo) component1.type();
            softly.assertThat(component1type.qualifiedName()).isEqualTo("int");
        });
    }
}
