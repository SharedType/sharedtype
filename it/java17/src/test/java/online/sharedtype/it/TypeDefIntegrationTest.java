package online.sharedtype.it;

import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.component.FieldComponentInfo;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

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
            softly.assertThat(classA.directSupertypes()).hasSize(1);
            ConcreteTypeInfo supertype1 = (ConcreteTypeInfo)classA.directSupertypes().get(0);
            softly.assertThat(supertype1.resolved()).isTrue();
            softly.assertThat(supertype1.qualifiedName()).isEqualTo("online.sharedtype.it.java8.SuperClassA");
            softly.assertThat(classA.resolved()).isTrue();

            softly.assertThat(classA.isCyclicReferenced()).isTrue();
            softly.assertThat(classA.isReferencedByAnnotated()).isTrue();
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
    void recursiveClass() {
        ClassDef recursiveClass = (ClassDef) deserializeTypeDef("online.sharedtype.it.java8.RecursiveClass.ser");
        assertThat(recursiveClass.simpleName()).isEqualTo("RecursiveClass");

        assertThat(recursiveClass.components()).hasSize(2);
        FieldComponentInfo component1 = recursiveClass.components().get(0);
        assertThat(component1.name()).isEqualTo("directRef");
        ConcreteTypeInfo field1TypeInfo = (ConcreteTypeInfo) component1.type();
        assertThat(field1TypeInfo.typeDef()).isEqualTo(recursiveClass);

        FieldComponentInfo component2 = recursiveClass.components().get(1);
        assertThat(component2.name()).isEqualTo("arrayRef");
        ArrayTypeInfo field2TypeInfo = (ArrayTypeInfo) component2.type();
        ConcreteTypeInfo arrayElementTypeInfo = (ConcreteTypeInfo) field2TypeInfo.component();
        assertThat(arrayElementTypeInfo.typeDef()).isEqualTo(recursiveClass);
    }
}
