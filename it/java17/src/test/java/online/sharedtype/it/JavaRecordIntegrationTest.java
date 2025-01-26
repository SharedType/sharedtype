package online.sharedtype.it;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static online.sharedtype.it.support.TypeDefDeserializer.deserializeTypeDef;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class JavaRecordIntegrationTest {
    private final ClassDef classDef = (ClassDef)deserializeTypeDef("online.sharedtype.it.java17.JavaRecord.ser");
    private final Map<String, FieldComponentInfo> componentByName = classDef.components().stream()
        .collect(Collectors.toMap(FieldComponentInfo::name, Function.identity()));

    @Test
    void typeVariables() {
        var typeParameters = classDef.typeVariables();
        assertThat(typeParameters).hasSize(1);

        assertThat(typeParameters.get(0).name()).isEqualTo("T");
    }

    @Test
    void directSupertypes() {
        var superTypes = classDef.directSupertypes();
        assertThat(superTypes).hasSize(1);
        var supertypeInfo = (ConcreteTypeInfo)superTypes.get(0);
        assertThat(supertypeInfo.resolved()).isTrue();
        assertThat(supertypeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.InterfaceA");
    }

    @Test
    void stringField() {
        var stringField = componentByName.get("string");
        var typeInfo = (ConcreteTypeInfo)stringField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void primitiveByteField() {
        var primitiveByteField = componentByName.get("primitiveByte");
        var typeInfo = (ConcreteTypeInfo)primitiveByteField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("byte");
    }

    @Test
    void boxedByteField() {
        var boxedByteField = componentByName.get("boxedByte");
        assertThat(boxedByteField.name()).isEqualTo("boxedByte");
        var typeInfo = (ConcreteTypeInfo)boxedByteField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Byte");
    }

    @Test
    void primitiveShortField() {
        var primitiveShortField = componentByName.get("primitiveShort");
        var typeInfo = (ConcreteTypeInfo)primitiveShortField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("short");
    }

    @Test
    void boxedShortField() {
        var boxedShortField = componentByName.get("boxedShort");
        var typeInfo = (ConcreteTypeInfo)boxedShortField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Short");
    }

    @Test
    void primitiveIntField() {
        var primitiveIntField = componentByName.get("primitiveInt");
        var typeInfo = (ConcreteTypeInfo)primitiveIntField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");
    }

    @Test
    void boxedIntField() {
        var boxedIntField = componentByName.get("boxedInt");
        var typeInfo = (ConcreteTypeInfo)boxedIntField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Integer");
    }

    @Test
    void primitiveLongField() {
        var primitiveLongField = componentByName.get("primitiveLong");
        var typeInfo = (ConcreteTypeInfo)primitiveLongField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("long");
    }

    @Test
    void boxedLongField() {
        var boxedLongField = componentByName.get("boxedLong");
        var typeInfo = (ConcreteTypeInfo)boxedLongField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Long");
    }

    @Test
    void primitiveFloatField() {
        var primitiveFloatField = componentByName.get("primitiveFloat");
        var typeInfo = (ConcreteTypeInfo)primitiveFloatField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("float");
    }

    @Test
    void boxedFloatField() {
        var boxedFloatField = componentByName.get("boxedFloat");
        var typeInfo = (ConcreteTypeInfo)boxedFloatField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Float");
    }

    @Test
    void primitiveDoubleField() {
        var primitiveDoubleField = componentByName.get("primitiveDouble");
        var typeInfo = (ConcreteTypeInfo)primitiveDoubleField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("double");
    }

    @Test
    void boxedDoubleField() {
        var boxedDoubleField = componentByName.get("boxedDouble");
        var typeInfo = (ConcreteTypeInfo)boxedDoubleField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Double");
    }

    @Test
    void primitiveBooleanField() {
        var primitiveBooleanField = componentByName.get("primitiveBoolean");
        var typeInfo = (ConcreteTypeInfo)primitiveBooleanField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("boolean");
    }

    @Test
    void boxedBooleanField() {
        var boxedBooleanField = componentByName.get("boxedBoolean");
        var typeInfo = (ConcreteTypeInfo)boxedBooleanField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Boolean");
    }

    @Test
    void primitiveCharField() {
        var primitiveCharField = componentByName.get("primitiveChar");
        var typeInfo = (ConcreteTypeInfo)primitiveCharField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("char");
    }

    @Test
    void boxedCharField() {
        var boxedCharField = componentByName.get("boxedChar");
        var typeInfo = (ConcreteTypeInfo)boxedCharField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Character");
    }

    @Test
    void objectField() {
        var objectField = componentByName.get("object");
        var typeInfo = (ConcreteTypeInfo)objectField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Object");
    }

//    @Disabled("not supported")
//    @Test
//    void aVoidField() {
//        var aVoidField = componentByName.get("aVoid");
//        var typeInfo = (ConcreteTypeInfo)aVoidField.type();
//        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Void");
//    }

    @Test
    void cyclicDependencyField() {
        var cyclicDependencyField = componentByName.get("cyclicDependency");
        var typeInfo = (ConcreteTypeInfo)cyclicDependencyField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.DependencyClassA");
    }

    @Test
    void containerStringListField() {
        var containerStringListField = componentByName.get("containerStringList");
        var arrayTypeInfo = (ArrayTypeInfo)containerStringListField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.Container");
        var typeArgInfo = (ConcreteTypeInfo)typeInfo.typeArgs().get(0);
        assertThat(typeArgInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void containerStringListCollectionField() {
        var containerStringListCollectionField = componentByName.get("containerStringListCollection");
        var arrayTypeInfo = (ArrayTypeInfo)containerStringListCollectionField.type();
        var nestedArrayTypeInfo = (ArrayTypeInfo)arrayTypeInfo.component();
        var typeInfo = (ConcreteTypeInfo)nestedArrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.Container");
        var typeArgInfo = (ConcreteTypeInfo)typeInfo.typeArgs().get(0);
        assertThat(typeArgInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void genericListField() {
        var genericListField = componentByName.get("genericList");
        var arrayTypeInfo = (ArrayTypeInfo)genericListField.type();
        var typeInfo = (TypeVariableInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java17.JavaRecord@T");
    }

    @Test
    void genericSetField() {
        var genericSetField = componentByName.get("genericSet");
        var arrayTypeInfo = (ArrayTypeInfo)genericSetField.type();
        var typeInfo = (TypeVariableInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java17.JavaRecord@T");
    }

    @Test
    void genericListSetField() {
        var genericListSetField = componentByName.get("genericListSet");
        var arrayTypeInfo = (ArrayTypeInfo)genericListSetField.type();
        var nestedArrayTypeInfo = (ArrayTypeInfo)arrayTypeInfo.component();
        var typeInfo = (TypeVariableInfo)nestedArrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java17.JavaRecord@T");
    }

    @Test
    void genericMapField() {
        // TODO
    }

    @Test
    void intArrayField() {
        var integerArrayField = componentByName.get("intArray");
        var arrayTypeInfo = (ArrayTypeInfo)integerArrayField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("int");
    }

    @Test
    void boxedIntArrayField() {
        var boxedIntArrayField = componentByName.get("boxedIntArray");
        var arrayTypeInfo = (ArrayTypeInfo)boxedIntArrayField.type();
        var typeInfo = (ConcreteTypeInfo)arrayTypeInfo.component();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.Integer");
    }

    @Test
    void enumGalaxyField() {
        var enumGalaxyField = componentByName.get("enumGalaxy");
        var typeInfo = (ConcreteTypeInfo)enumGalaxyField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumGalaxy");
    }

    @Test
    void enumSizeField() {
        var enumSizeField = componentByName.get("enumSize");
        var typeInfo = (ConcreteTypeInfo)enumSizeField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("online.sharedtype.it.java8.EnumSize");
    }

    @Test
    void duplicateAccessorField() {
        var duplicateAccessorField = componentByName.get("duplicateAccessor");
        var typeInfo = (ConcreteTypeInfo)duplicateAccessorField.type();
        assertThat(typeInfo.qualifiedName()).isEqualTo("java.lang.String");
    }

    @Test
    void implementedMethodGetValueFromInterface() {
        var method = componentByName.get("value");
        var typeInfo = (TypeVariableInfo)method.type();
        assertThat(typeInfo.name()).isEqualTo("T");
    }

    @Test
    void fieldsSize() {
        assertThat(classDef.components().size()).isEqualTo(30);
    }
}
