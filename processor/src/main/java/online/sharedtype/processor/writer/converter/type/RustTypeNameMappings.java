package online.sharedtype.processor.writer.converter.type;

import lombok.experimental.UtilityClass;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
final class RustTypeNameMappings {
    private static final Map<TypeInfo, String> typeNameMappings = new HashMap<>(32);
    static {
        typeNameMappings.put(Constants.BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BYTE_TYPE_INFO, "i8");
        typeNameMappings.put(Constants.CHAR_TYPE_INFO, "char");
        typeNameMappings.put(Constants.DOUBLE_TYPE_INFO, "f64");
        typeNameMappings.put(Constants.FLOAT_TYPE_INFO, "f32");
        typeNameMappings.put(Constants.INT_TYPE_INFO, "i32");
        typeNameMappings.put(Constants.LONG_TYPE_INFO, "i64");
        typeNameMappings.put(Constants.SHORT_TYPE_INFO, "i16");

        typeNameMappings.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        typeNameMappings.put(Constants.BOXED_BYTE_TYPE_INFO, "i8");
        typeNameMappings.put(Constants.BOXED_CHAR_TYPE_INFO, "char");
        typeNameMappings.put(Constants.BOXED_DOUBLE_TYPE_INFO, "f64");
        typeNameMappings.put(Constants.BOXED_FLOAT_TYPE_INFO, "f32");
        typeNameMappings.put(Constants.BOXED_INT_TYPE_INFO, "i32");
        typeNameMappings.put(Constants.BOXED_LONG_TYPE_INFO, "i64");
        typeNameMappings.put(Constants.BOXED_SHORT_TYPE_INFO, "i16");
        typeNameMappings.put(Constants.BIG_INTEGER_TYPE_INFO, "String");
        typeNameMappings.put(Constants.BIG_DECIMAL_TYPE_INFO, "String");

        typeNameMappings.put(Constants.STRING_TYPE_INFO, "String");
        typeNameMappings.put(Constants.VOID_TYPE_INFO, "!");
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, "Box<dyn Any>");
    }

    public static String getOrDefault(TypeInfo typeInfo, String defaultExpr) {
        return typeNameMappings.getOrDefault(typeInfo, defaultExpr);
    }
}
