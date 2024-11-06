package online.sharedtype.it;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.EnumValueInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

final class TypeDefIntegrationTest {
    @Test
    void container() {
        ClassDef container = (ClassDef) deserializeTypeDef("online.sharedtype.it.java8.Container.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(container.simpleName()).isEqualTo("Container");
            softly.assertThat(container.qualifiedName()).isEqualTo("online.sharedtype.it.java8.Container");
            softly.assertThat(container.components()).hasSize(1);
            FieldComponentInfo component1 = container.components().get(0);
            TypeVariableInfo field1Type = (TypeVariableInfo) component1.type();
            softly.assertThat(field1Type.name()).isEqualTo("T");
            softly.assertThat(component1.name()).isEqualTo("t");

            softly.assertThat(container.resolved()).isTrue();
            softly.assertThat(container.typeVariables()).hasSize(1);
            TypeVariableInfo typeVariable1 = container.typeVariables().get(0);
            softly.assertThat(typeVariable1.name()).isEqualTo("T");
        });
    }

    @Test
    void dependencyClassA() {
        ClassDef classA = (ClassDef) deserializeTypeDef("online.sharedtype.it.java8.DependencyClassA.ser");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(classA.simpleName()).isEqualTo("DependencyClassA");
            softly.assertThat(classA.qualifiedName()).isEqualTo("online.sharedtype.it.java8.DependencyClassA");
            softly.assertThat(classA.components()).hasSize(1);

            FieldComponentInfo component1 = classA.components().get(0);
            softly.assertThat(component1.optional()).isFalse();
            softly.assertThat(component1.name()).isEqualTo("b");
            softly.assertThat(component1.type().resolved()).isTrue();
            ConcreteTypeInfo component1type = (ConcreteTypeInfo) component1.type();
            softly.assertThat(component1type.qualifiedName()).isEqualTo("online.sharedtype.it.java8.DependencyClassB");

            softly.assertThat(classA.typeVariables()).isEmpty();
            softly.assertThat(classA.supertypes()).hasSize(1);
            ConcreteTypeInfo supertype1 = (ConcreteTypeInfo)classA.supertypes().get(0);
            softly.assertThat(supertype1.resolved()).isTrue();
            softly.assertThat(supertype1.qualifiedName()).isEqualTo("online.sharedtype.it.java8.SuperClassA");
            softly.assertThat(classA.resolved()).isTrue();
        });
    }

    @Test
    void dependencyClassB() {
        ClassDef classB = (ClassDef) deserializeTypeDef("online.sharedtype.it.java8.DependencyClassB.ser");
        assertThat(classB.simpleName()).isEqualTo("DependencyClassB");
    }

    @Test
    void dependencyClassC() {
        ClassDef classC = (ClassDef) deserializeTypeDef("online.sharedtype.it.java8.DependencyClassC.ser");
        assertThat(classC.simpleName()).isEqualTo("DependencyClassC");
    }

    @Test
    void enumGalaxy() {
        EnumDef enumGalaxy = (EnumDef) deserializeTypeDef("online.sharedtype.it.java8.EnumGalaxy.ser");
        assertThat(enumGalaxy.simpleName()).isEqualTo("EnumGalaxy");
        assertThat(enumGalaxy.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumGalaxy");
        assertThat(enumGalaxy.components()).hasSize(3).allMatch(constant -> {
            ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)constant.type();
            return typeInfo.qualifiedName().equals("java.lang.String");
        });
        EnumValueInfo constant1 = enumGalaxy.components().get(0);
        assertThat(constant1.value()).isEqualTo("MilkyWay");

        EnumValueInfo constant2 = enumGalaxy.components().get(1);
        assertThat(constant2.value()).isEqualTo("Andromeda");

        EnumValueInfo constant3 = enumGalaxy.components().get(2);
        assertThat(constant3.value()).isEqualTo("Triangulum");
    }

    @Test
    void enumSize() {
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
