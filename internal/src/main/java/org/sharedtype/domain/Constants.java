package org.sharedtype.domain;

import org.sharedtype.annotation.SharedType;

import javax.lang.model.type.TypeKind;
import java.util.Map;

public final class Constants {
    public static final String ANNOTATION_QUALIFIED_NAME = SharedType.class.getName();

    public static final Map<TypeKind, ConcreteTypeInfo> PRIMITIVES = Map.of(
        TypeKind.BOOLEAN, ConcreteTypeInfo.ofPredefined("boolean"),
        TypeKind.BYTE, ConcreteTypeInfo.ofPredefined("byte"),
        TypeKind.CHAR, ConcreteTypeInfo.ofPredefined("char"),
        TypeKind.DOUBLE, ConcreteTypeInfo.ofPredefined("double"),
        TypeKind.FLOAT, ConcreteTypeInfo.ofPredefined("float"),
        TypeKind.INT, ConcreteTypeInfo.ofPredefined("int"),
        TypeKind.LONG, ConcreteTypeInfo.ofPredefined("long"),
        TypeKind.SHORT, ConcreteTypeInfo.ofPredefined("short")
    );

    public static final ConcreteTypeInfo STRING_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.String");
    public static final Map<String, ConcreteTypeInfo> PREDEFINED_OBJECT_TYPES = Map.ofEntries(
        Map.entry("java.lang.Boolean", ConcreteTypeInfo.ofPredefined("java.lang.Boolean")),
        Map.entry("java.lang.Byte", ConcreteTypeInfo.ofPredefined("java.lang.Byte")),
        Map.entry("java.lang.Character", ConcreteTypeInfo.ofPredefined("java.lang.Character")),
        Map.entry("java.lang.Double", ConcreteTypeInfo.ofPredefined("java.lang.Double")),
        Map.entry("java.lang.Float", ConcreteTypeInfo.ofPredefined("java.lang.Float")),
        Map.entry("java.lang.Integer", ConcreteTypeInfo.ofPredefined("java.lang.Integer")),
        Map.entry("java.lang.Long", ConcreteTypeInfo.ofPredefined("java.lang.Long")),
        Map.entry("java.lang.Short", ConcreteTypeInfo.ofPredefined("java.lang.Short")),
        Map.entry("java.lang.String", STRING_TYPE_INFO),
        Map.entry("java.lang.Void", ConcreteTypeInfo.ofPredefined("java.lang.Void")),
        Map.entry("java.lang.Object", ConcreteTypeInfo.ofPredefined("java.lang.Object")),
        Map.entry("java.lang.Class", ConcreteTypeInfo.ofPredefined("java.lang.Class")),
        Map.entry("java.lang.Enum", ConcreteTypeInfo.ofPredefined("java.lang.Enum")),
        Map.entry("java.util.Optional", ConcreteTypeInfo.ofPredefined("java.util.Optional")),
        Map.entry("java.util.Map", ConcreteTypeInfo.ofPredefined("java.util.Map")) // TODO: Map support
    );

    private Constants() {
    }
}
