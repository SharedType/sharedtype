package org.sharedtype.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sharedtype.domain.TypeDefDeserializer.deserializeTypeDef;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JavaRecordIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("JavaRecord.ser");

    @Test
    void typeVariables() {
        var typeParameters = classDef.typeVariables();
        assertThat(typeParameters).hasSize(2);

        assertThat(typeParameters.get(0).getName()).isEqualTo("T");
        assertThat(typeParameters.get(1).getName()).isEqualTo("K");
    }

    @Test
    void supertypes() {
        var superTypes = classDef.supertypes();
        assertThat(superTypes).hasSize(1);
        var supertypeInfo = (ConcreteTypeInfo)superTypes.get(0);
        assertThat(supertypeInfo.resolved()).isTrue();
        assertThat(supertypeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.types.InterfaceA");
    }

    @Test
    void duplicateAccessorField() {
        var duplicateAccessorField = classDef.components().get(0);
        assertThat(duplicateAccessorField.name()).isEqualTo("duplicateAccessor");
        var typeInfo = (ConcreteTypeInfo)duplicateAccessorField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void stringField() {
        var stringField = classDef.components().get(1);
        assertThat(stringField.name()).isEqualTo("string");
        var typeInfo = (ConcreteTypeInfo)stringField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void primitiveByteField() {
        var primitiveByteField = classDef.components().get(2);
        assertThat(primitiveByteField.name()).isEqualTo("primitiveByte");
        var typeInfo = (ConcreteTypeInfo)primitiveByteField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("byte");
    }

    @Test
    void boxedByteField() {
        var boxedByteField = classDef.components().get(3);
        assertThat(boxedByteField.name()).isEqualTo("boxedByte");
        var typeInfo = (ConcreteTypeInfo)boxedByteField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Byte");
    }

    @Test
    void primitiveShortField() {
        var primitiveShortField = classDef.components().get(4);
        assertThat(primitiveShortField.name()).isEqualTo("primitiveShort");
        var typeInfo = (ConcreteTypeInfo)primitiveShortField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("short");
    }

    @Test
    void boxedShortField() {
        var boxedShortField = classDef.components().get(5);
        assertThat(boxedShortField.name()).isEqualTo("boxedShort");
        var typeInfo = (ConcreteTypeInfo)boxedShortField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Short");
    }

    @Test
    void primitiveIntField() {
        var primitiveIntField = classDef.components().get(6);
        assertThat(primitiveIntField.name()).isEqualTo("primitiveInt");
        var typeInfo = (ConcreteTypeInfo)primitiveIntField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");
    }

    @Test
    void boxedIntField() {
        var boxedIntField = classDef.components().get(7);
        assertThat(boxedIntField.name()).isEqualTo("boxedInt");
        var typeInfo = (ConcreteTypeInfo)boxedIntField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Integer");
    }

    @Test
    void primitiveLongField() {
        var primitiveLongField = classDef.components().get(8);
        assertThat(primitiveLongField.name()).isEqualTo("primitiveLong");
        var typeInfo = (ConcreteTypeInfo)primitiveLongField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("long");
    }

    @Test
    void boxedLongField() {
        var boxedLongField = classDef.components().get(9);
        assertThat(boxedLongField.name()).isEqualTo("boxedLong");
        var typeInfo = (ConcreteTypeInfo)boxedLongField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Long");
    }

    @Test
    void primitiveFloatField() {
        var primitiveFloatField = classDef.components().get(10);
        assertThat(primitiveFloatField.name()).isEqualTo("primitiveFloat");
        var typeInfo = (ConcreteTypeInfo)primitiveFloatField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("float");
    }

    @Test
    void boxedFloatField() {
        var boxedFloatField = classDef.components().get(11);
        assertThat(boxedFloatField.name()).isEqualTo("boxedFloat");
        var typeInfo = (ConcreteTypeInfo)boxedFloatField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Float");
    }

    @Test
    void primitiveDoubleField() {
        var primitiveDoubleField = classDef.components().get(12);
        assertThat(primitiveDoubleField.name()).isEqualTo("primitiveDouble");
        var typeInfo = (ConcreteTypeInfo)primitiveDoubleField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("double");
    }

    @Test
    void boxedDoubleField() {
        var boxedDoubleField = classDef.components().get(13);
        assertThat(boxedDoubleField.name()).isEqualTo("boxedDouble");
        var typeInfo = (ConcreteTypeInfo)boxedDoubleField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Double");
    }

    @Test
    void primitiveBooleanField() {
        var primitiveBooleanField = classDef.components().get(14);
        assertThat(primitiveBooleanField.name()).isEqualTo("primitiveBoolean");
        var typeInfo = (ConcreteTypeInfo)primitiveBooleanField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("boolean");
    }

    @Test
    void boxedBooleanField() {
        var boxedBooleanField = classDef.components().get(15);
        assertThat(boxedBooleanField.name()).isEqualTo("boxedBoolean");
        var typeInfo = (ConcreteTypeInfo)boxedBooleanField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Boolean");
    }

    @Test
    void primitiveCharField() {
        var primitiveCharField = classDef.components().get(16);
        assertThat(primitiveCharField.name()).isEqualTo("primitiveChar");
        var typeInfo = (ConcreteTypeInfo)primitiveCharField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("char");
    }

    @Test
    void boxedCharField() {
        var boxedCharField = classDef.components().get(17);
        assertThat(boxedCharField.name()).isEqualTo("boxedChar");
        var typeInfo = (ConcreteTypeInfo)boxedCharField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Character");
    }

    @Test
    void objectField() {
        var objectField = classDef.components().get(18);
        assertThat(objectField.name()).isEqualTo("object");
        var typeInfo = (ConcreteTypeInfo)objectField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Object");
    }

    @Test
    void aVoidField() {
        var aVoidField = classDef.components().get(19);
        assertThat(aVoidField.name()).isEqualTo("aVoid");
        var typeInfo = (ConcreteTypeInfo)aVoidField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Void");
    }

    @Test
    void cyclicDependencyField() {
        var cyclicDependencyField = classDef.components().get(20);
        assertThat(cyclicDependencyField.name()).isEqualTo("cyclicDependency");
        var typeInfo = (ConcreteTypeInfo)cyclicDependencyField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.types.DependencyClassA");
    }

    @Test
    void containerStringListField() {
        var containerStringListField = classDef.components().get(21);
        assertThat(containerStringListField.name()).isEqualTo("containerStringList");
        var arrayTypeInfo = (ArrayTypeInfo)containerStringListField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.types.Container");
        var typeArgInfo = (ConcreteTypeInfo)typeInfo.typeArgs().get(0);
        assertThat(typeArgInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void containerStringListCollectionField() {
        var containerStringListCollectionField = classDef.components().get(22);
        assertThat(containerStringListCollectionField.name()).isEqualTo("containerStringListCollection");
        var arrayTypeInfo = (ArrayTypeInfo)containerStringListCollectionField.type();
        var nestedArrayTypeInfo = (ArrayTypeInfo)arrayTypeInfo.component();
        var typeInfo = (ConcreteTypeInfo)nestedArrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("org.sharedtype.it.types.Container");
        var typeArgInfo = (ConcreteTypeInfo)typeInfo.typeArgs().get(0);
        assertThat(typeArgInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void genericListField() {
        var genericListField = classDef.components().get(23);
        assertThat(genericListField.name()).isEqualTo("genericList");
        var arrayTypeInfo = (ArrayTypeInfo)genericListField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("T");
    }

    @Test
    void genericSetField() {
        var genericSetField = classDef.components().get(24);
        assertThat(genericSetField.name()).isEqualTo("genericSet");
        var arrayTypeInfo = (ArrayTypeInfo)genericSetField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("T");
    }

    @Test
    void genericListSetField() {
        var genericListSetField = classDef.components().get(25);
        assertThat(genericListSetField.name()).isEqualTo("genericListSet");
        var arrayTypeInfo = (ArrayTypeInfo)genericListSetField.type();
        var nestedArrayTypeInfo = (ArrayTypeInfo)arrayTypeInfo.component();
        var typeInfo = (ConcreteTypeInfo)nestedArrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("T");
    }

    @Test
    void intArray() {
        var integerArrayField = classDef.components().get(27);
        assertThat(integerArrayField.name()).isEqualTo("intArray");
        var arrayTypeInfo = (ArrayTypeInfo)integerArrayField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");
    }

    @Test
    void boxedIntArray() {
        var boxedIntArrayField = classDef.components().get(28);
        assertThat(boxedIntArrayField.name()).isEqualTo("boxedIntArray");
        var arrayTypeInfo = (ArrayTypeInfo)boxedIntArrayField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Integer");
    }

    @Test
    void fieldsSize() {
        assertThat(classDef.components().size()).isEqualTo(29);
    }
}
