package org.sharedtype.it;

import org.junit.jupiter.api.Test;
import org.sharedtype.domain.ClassDef;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.FieldComponentInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.it.support.TypeDefDeserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;

final class JavaClassIntegrationTest {
    @Test
    void javaClass() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("org.sharedtype.it.java8.JavaClass.ser");
        assertThat(classDef.components()).satisfiesExactly(
            string -> {
                assertThat(string.name()).isEqualTo("string");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)string.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
            },
            size -> {
                assertThat(size.name()).isEqualTo("size");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)size.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.java8.EnumSize");
            },
            notIgnoredImplementedMethod -> {
                assertThat(notIgnoredImplementedMethod.name()).isEqualTo("notIgnoredImplementedMethod");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)notIgnoredImplementedMethod.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("int");
            }
        );

        assertThat(classDef.supertypes()).hasSize(1);
        ConcreteTypeInfo superType = (ConcreteTypeInfo)classDef.supertypes().get(0);
        assertThat(superType.qualifiedName()).isEqualTo("org.sharedtype.it.java8.SuperClassA");
    }

    @Test
    void anotherJavaClassShouldBeRenamed() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("org.sharedtype.it.java8.other.JavaClass.ser");
        assertThat(classDef.simpleName()).isEqualTo("AnotherJavaClass");
        assertThat(classDef.components()).satisfiesExactly(
            value -> {
                assertThat(value.name()).isEqualTo("value");
                ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)value.type();
                assertThat(typeInfo.qualifiedName()).isEqualTo("int");
            }
        );
    }

    @Test
    void superClassA() {
        ClassDef classDef = (ClassDef) deserializeTypeDef("org.sharedtype.it.java8.SuperClassA.ser");
        assertThat(classDef.simpleName()).isEqualTo("SuperClassA");

        assertThat(classDef.supertypes()).hasSize(1);
        ConcreteTypeInfo superType = (ConcreteTypeInfo)classDef.supertypes().get(0);
        assertThat(superType.qualifiedName()).isEqualTo("org.sharedtype.it.java8.InterfaceA");

        assertThat(classDef.typeVariables()).isEmpty();
        assertThat(classDef.components()).hasSize(3);

        FieldComponentInfo component1 = classDef.components().get(0);
        assertThat(component1.name()).isEqualTo("a");
        ConcreteTypeInfo typeInfo = (ConcreteTypeInfo)component1.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");

        FieldComponentInfo component2 = classDef.components().get(1);
        assertThat(component2.name()).isEqualTo("value");
        typeInfo = (ConcreteTypeInfo)component2.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Integer");

        FieldComponentInfo component3 = classDef.components().get(2);
        assertThat(component3.name()).isEqualTo("notIgnoredImplementedMethod");
        typeInfo = (ConcreteTypeInfo)component3.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");
    }

    @Test
    void interfaceA() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("org.sharedtype.it.java8.InterfaceA.ser");
        assertThat(classDef.simpleName()).isEqualTo("InterfaceA");
        assertThat(classDef.qualifiedName()).isEqualTo("org.sharedtype.it.java8.InterfaceA");
        assertThat(classDef.components()).satisfiesExactly(
            value -> {
                assertThat(value.name()).isEqualTo("value");
                TypeVariableInfo typeInfo = (TypeVariableInfo)value.type();
                assertThat(typeInfo.name()).isEqualTo("T");
            }
        );
    }

    @Test
    void shouldNotGenerateIgnoredTypes() {
        assertThat(TypeDefDeserializer.doesResourceExist("org.sharedtype.it.java8.IgnoredInterfaceB.ser")).isFalse();
        assertThat(TypeDefDeserializer.doesResourceExist("org.sharedtype.it.java8.IgnoredSuperClassB.ser")).isFalse();
    }

    @Test
    void innerClass() {
        ClassDef classDef = (ClassDef)deserializeTypeDef("org.sharedtype.it.java8.JavaClass.InnerClass.ser");
        assertThat(classDef.simpleName()).isEqualTo("InnerClass");
        assertThat(classDef.qualifiedName()).isEqualTo("org.sharedtype.it.java8.JavaClass.InnerClass");
        assertThat(classDef.components()).satisfiesExactly(
            value -> {
                assertThat(value.name()).isEqualTo("value");
                ConcreteTypeInfo fieldTypeInfo = (ConcreteTypeInfo)value.type();
                assertThat(fieldTypeInfo.qualifiedName()).isEqualTo("int");
            }
        );
    }
}
