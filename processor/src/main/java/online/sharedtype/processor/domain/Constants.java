package online.sharedtype.processor.domain;

import online.sharedtype.SharedType;

import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cause Chung
 */
public final class Constants {
    public static final String ANNOTATION_QUALIFIED_NAME = SharedType.class.getName();

    public static final ConcreteTypeInfo BOOLEAN_TYPE_INFO = ConcreteTypeInfo.ofPredefined("boolean", "boolean");
    public static final ConcreteTypeInfo BYTE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("byte", "byte");
    public static final ConcreteTypeInfo CHAR_TYPE_INFO = ConcreteTypeInfo.ofPredefined("char", "char");
    public static final ConcreteTypeInfo DOUBLE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("double", "double");
    public static final ConcreteTypeInfo FLOAT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("float", "float");
    public static final ConcreteTypeInfo INT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("int", "int");
    public static final ConcreteTypeInfo LONG_TYPE_INFO = ConcreteTypeInfo.ofPredefined("long", "long");
    public static final ConcreteTypeInfo SHORT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("short", "short");

    public static final ConcreteTypeInfo BOXED_BOOLEAN_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Boolean", "Boolean");
    public static final ConcreteTypeInfo BOXED_BYTE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Byte", "Byte");
    public static final ConcreteTypeInfo BOXED_CHAR_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Character", "Character");
    public static final ConcreteTypeInfo BOXED_DOUBLE_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Double", "Double");
    public static final ConcreteTypeInfo BOXED_FLOAT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Float", "Float");
    public static final ConcreteTypeInfo BOXED_INT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Integer", "Integer");
    public static final ConcreteTypeInfo BOXED_LONG_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Long", "Long");
    public static final ConcreteTypeInfo BOXED_SHORT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Short", "Short");
    public static final ConcreteTypeInfo STRING_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.String", "String");
    public static final ConcreteTypeInfo VOID_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Void", "Void");
    public static final ConcreteTypeInfo OBJECT_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Object", "Object");
    public static final ConcreteTypeInfo CLASS_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Class", "Class");
    public static final ConcreteTypeInfo ENUM_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.lang.Enum", "Enum");
    public static final ConcreteTypeInfo OPTIONAL_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.util.Optional", "Optional");
    public static final ConcreteTypeInfo MAP_TYPE_INFO = ConcreteTypeInfo.ofPredefined("java.util.Map", "Map");

    public static final Map<TypeKind, ConcreteTypeInfo> PRIMITIVES = new HashMap<>(8);
    static {
        PRIMITIVES.put(TypeKind.BOOLEAN, BOOLEAN_TYPE_INFO);
        PRIMITIVES.put(TypeKind.BYTE, BYTE_TYPE_INFO);
        PRIMITIVES.put(TypeKind.CHAR, CHAR_TYPE_INFO);
        PRIMITIVES.put(TypeKind.DOUBLE, DOUBLE_TYPE_INFO);
        PRIMITIVES.put(TypeKind.FLOAT, FLOAT_TYPE_INFO);
        PRIMITIVES.put(TypeKind.INT, INT_TYPE_INFO);
        PRIMITIVES.put(TypeKind.LONG, LONG_TYPE_INFO);
        PRIMITIVES.put(TypeKind.SHORT, SHORT_TYPE_INFO);
    }

    public static final Map<String, ConcreteTypeInfo> PREDEFINED_OBJECT_TYPES = new HashMap<>(16);
    static {
        PREDEFINED_OBJECT_TYPES.put("java.lang.Boolean", BOXED_BOOLEAN_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Byte", BOXED_BYTE_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Character", BOXED_CHAR_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Double", BOXED_DOUBLE_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Float", BOXED_FLOAT_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Integer", BOXED_INT_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Long", BOXED_LONG_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Short", BOXED_SHORT_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.String", STRING_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Void", VOID_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Object", OBJECT_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Class", CLASS_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.lang.Enum", ENUM_TYPE_INFO);
        PREDEFINED_OBJECT_TYPES.put("java.util.Optional", OPTIONAL_TYPE_INFO);
        // PREDEFINED_OBJECT_TYPES.put("java.util.Map", MAP_TYPE_INFO); // TODO: Map support
    };

    private Constants() {
    }
}
