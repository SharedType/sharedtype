package org.sharedtype.it;

import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.domain.TypeVariableInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.it.TypeDefDeserializer.deserializeTypeDef;

final class TypeDefIntegrationTest {
    @Test
    void container() {
        ClassDef container = (ClassDef) deserializeTypeDef("Container.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(container.simpleName()).isEqualTo("Container");
            softly.assertThat(container.qualifiedName()).isEqualTo("org.sharedtype.it.java8.Container");
            softly.assertThat(container.components()).hasSize(1);
            val component1 = container.components().get(0);
            val field1Type = (TypeVariableInfo) component1.type();
            softly.assertThat(field1Type.getName()).isEqualTo("T");
            softly.assertThat(component1.name()).isEqualTo("t");

            softly.assertThat(container.resolved()).isTrue();
            softly.assertThat(container.typeVariables()).hasSize(1);
            val typeVariable1 = container.typeVariables().get(0);
            softly.assertThat(typeVariable1.getName()).isEqualTo("T");
        });
    }

    @Test
    void dependencyClassA() {
        ClassDef classA = (ClassDef) deserializeTypeDef("DependencyClassA.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(classA.simpleName()).isEqualTo("DependencyClassA");
            softly.assertThat(classA.qualifiedName()).isEqualTo("org.sharedtype.it.java8.DependencyClassA");
            softly.assertThat(classA.components()).hasSize(1);

            val component1 = classA.components().get(0);
            softly.assertThat(component1.optional()).isFalse();
            softly.assertThat(component1.name()).isEqualTo("b");
            softly.assertThat(component1.type().resolved()).isTrue();
            val component1type = (ConcreteTypeInfo) component1.type();
            softly.assertThat(component1type.qualifiedName()).isEqualTo("org.sharedtype.it.java8.DependencyClassB");

            softly.assertThat(classA.typeVariables()).isEmpty();
            softly.assertThat(classA.supertypes()).hasSize(1);
            val supertype1 = (ConcreteTypeInfo)classA.supertypes().get(0);
            softly.assertThat(supertype1.resolved()).isTrue();
            softly.assertThat(supertype1.qualifiedName()).isEqualTo("org.sharedtype.it.java8.SuperClassA");
            softly.assertThat(classA.resolved()).isTrue();
        });
    }

    @Test
    void dependencyClassB() {
        ClassDef classB = (ClassDef) deserializeTypeDef("DependencyClassB.ser");
        assertThat(classB.simpleName()).isEqualTo("DependencyClassB");
    }

    @Test
    void dependencyClassC() {
        ClassDef classC = (ClassDef) deserializeTypeDef("DependencyClassC.ser");
        assertThat(classC.simpleName()).isEqualTo("DependencyClassC");
    }

    @Test
    void enumGalaxy() {
        EnumDef enumGalaxy = (EnumDef) deserializeTypeDef("EnumGalaxy.ser");
        assertThat(enumGalaxy.simpleName()).isEqualTo("EnumGalaxy");
        assertThat(enumGalaxy.qualifiedName()).isEqualTo("org.sharedtype.it.java8.EnumGalaxy");
        assertThat(enumGalaxy.components()).hasSize(3).allMatch(constant -> {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("java.lang.String");
        });
        val constant1 = enumGalaxy.components().get(0);
        assertThat(constant1.value()).isEqualTo("MilkyWay");

        val constant2 = enumGalaxy.components().get(1);
        assertThat(constant2.value()).isEqualTo("Andromeda");

        val constant3 = enumGalaxy.components().get(2);
        assertThat(constant3.value()).isEqualTo("Triangulum");
    }

    @Test
    void enumSize() {
        EnumDef enumSize = (EnumDef) deserializeTypeDef("EnumSize.ser");
        assertThat(enumSize.simpleName()).isEqualTo("EnumSize");
        assertThat(enumSize.qualifiedName()).isEqualTo("org.sharedtype.it.java8.EnumSize");
        assertThat(enumSize.components()).hasSize(3).allMatch(constant -> {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("int");
        });

        val constant1 = enumSize.components().get(0);
        assertThat(constant1.value()).isEqualTo(1);

        val constant2 = enumSize.components().get(1);
        assertThat(constant2.value()).isEqualTo(2);

        val constant3 = enumSize.components().get(2);
        assertThat(constant3.value()).isEqualTo(3);
    }

    @Test
    void interfaceA() {
        ClassDef interfaceA = (ClassDef) deserializeTypeDef("InterfaceA.ser");
        assertThat(interfaceA.simpleName()).isEqualTo("InterfaceA");
    }

    @Test
    void superClassA() {
        ClassDef superClassA = (ClassDef) deserializeTypeDef("SuperClassA.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(superClassA.simpleName()).isEqualTo("SuperClassA");
            softly.assertThat(superClassA.components()).hasSize(1);
            val component1 = superClassA.components().get(0);
            softly.assertThat(component1.name()).isEqualTo("a");
            val component1type = (ConcreteTypeInfo) component1.type();
            softly.assertThat(component1type.qualifiedName()).isEqualTo("int");
        });
    }
}
