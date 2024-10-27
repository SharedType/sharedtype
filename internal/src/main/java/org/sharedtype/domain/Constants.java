package org.sharedtype.domain;

import org.sharedtype.annotation.SharedType;

import javax.lang.model.type.TypeKind;
import java.util.Map;

public final class Constants {
    public static final String ANNOTATION_QUALIFIED_NAME = SharedType.class.getName();

    public static final ConcreteTypeInfo BOOLEAN_TYPE_INFO = ConcreteTypeInfo.ofPredefined("boolean");
    public static final ConcreteTypeInfo BYTE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("byte");
    public static final ConcreteTypeInfo CHAR_TYPE_INFO = ConcreteTypeInfo.ofPredefined("char");
    public static final ConcreteTypeInfo DOUBLE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("double");
    public static final ConcreteTypeInfo FLOAT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("float");
    public static final ConcreteTypeInfo INT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("int");
    public static final ConcreteTypeInfo LONG_TYPE_INFO = ConcreteTypeInfo.ofPredefined("long");
    public static final ConcreteTypeInfo SHORT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("short");

    public static final ConcreteTypeInfo BOXED_BOOLEAN_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Boolean");
    public static final ConcreteTypeInfo BOXED_BYTE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Byte");
    public static final ConcreteTypeInfo BOXED_CHAR_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Character");
    public static final ConcreteTypeInfo BOXED_DOUBLE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Double");
    public static final ConcreteTypeInfo BOXED_FLOAT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Float");
    public static final ConcreteTypeInfo BOXED_INT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Integer");
    public static final ConcreteTypeInfo BOXED_LONG_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Long");
    public static final ConcreteTypeInfo BOXED_SHORT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Short");
    public static final ConcreteTypeInfo STRING_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.String");
    public static final ConcreteTypeInfo VOID_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Void");
    public static final ConcreteTypeInfo OBJECT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Object");
    public static final ConcreteTypeInfo CLASS_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Class");
    public static final ConcreteTypeInfo ENUM_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Enum");
    public static final ConcreteTypeInfo OPTIONAL_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.util.Optional");
    public static final ConcreteTypeInfo MAP_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.util.Map");

    public static final Map<TypeKind, ConcreteTypeInfo> PRIMITIVES = Map.of(
        TypeKind.BOOLEAN, BOOLEAN_TYPE_INFO,
        TypeKind.BYTE, BYTE_TYPE_INFO,
        TypeKind.CHAR, CHAR_TYPE_INFO,
        TypeKind.DOUBLE, DOUBLE_TYPE_INFO,
        TypeKind.FLOAT, FLOAT_TYPE_INFO,
        TypeKind.INT, INT_TYPE_INFO,
        TypeKind.LONG, LONG_TYPE_INFO,
        TypeKind.SHORT, SHORT_TYPE_INFO
    );

    public static final Map<String, ConcreteTypeInfo> PREDEFINED_OBJECT_TYPES = Map.ofEntries(
        Map.entry("java.lang.Boolean", BOXED_BOOLEAN_TYPE_INFO),
        Map.entry("java.lang.Byte", BOXED_BYTE_TYPE_INFO),
        Map.entry("java.lang.Character", BOXED_CHAR_TYPE_INFO),
        Map.entry("java.lang.Double", BOXED_DOUBLE_TYPE_INFO),
        Map.entry("java.lang.Float", BOXED_FLOAT_TYPE_INFO),
        Map.entry("java.lang.Integer", BOXED_INT_TYPE_INFO),
        Map.entry("java.lang.Long", BOXED_LONG_TYPE_INFO),
        Map.entry("java.lang.Short", BOXED_SHORT_TYPE_INFO),
        Map.entry("java.lang.String", STRING_TYPE_INFO),
        Map.entry("java.lang.Void", VOID_TYPE_INFO),
        Map.entry("java.lang.Object", OBJECT_TYPE_INFO),
        Map.entry("java.lang.Class", CLASS_TYPE_INFO),
        Map.entry("java.lang.Enum", ENUM_TYPE_INFO),
        Map.entry("java.util.Optional", OPTIONAL_TYPE_INFO),
        Map.entry("java.util.Map", MAP_TYPE_INFO) // TODO: Map support
    );

    private Constants() {
    }
}
