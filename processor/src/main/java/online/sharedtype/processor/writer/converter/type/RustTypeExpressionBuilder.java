package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class RustTypeExpressionBuilder implements TypeExpressionBuilder {
    private final Map<ConcreteTypeInfo, String> typeNameMappings;

    RustTypeExpressionBuilder() {
        Map<ConcreteTypeInfo, String> tempMap = new HashMap<>(20);
        tempMap.put(Constants.BOOLEAN_TYPE_INFO, "bool");
        tempMap.put(Constants.BYTE_TYPE_INFO, "i8");
        tempMap.put(Constants.CHAR_TYPE_INFO, "char");
        tempMap.put(Constants.DOUBLE_TYPE_INFO, "f64");
        tempMap.put(Constants.FLOAT_TYPE_INFO, "f32");
        tempMap.put(Constants.INT_TYPE_INFO, "i32");
        tempMap.put(Constants.LONG_TYPE_INFO, "i64");
        tempMap.put(Constants.SHORT_TYPE_INFO, "i16");

        tempMap.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        tempMap.put(Constants.BOXED_BYTE_TYPE_INFO, "i8");
        tempMap.put(Constants.BOXED_CHAR_TYPE_INFO, "char");
        tempMap.put(Constants.BOXED_DOUBLE_TYPE_INFO, "f64");
        tempMap.put(Constants.BOXED_FLOAT_TYPE_INFO, "f32");
        tempMap.put(Constants.BOXED_INT_TYPE_INFO, "i32");
        tempMap.put(Constants.BOXED_LONG_TYPE_INFO, "i64");
        tempMap.put(Constants.BOXED_SHORT_TYPE_INFO, "i16");

        tempMap.put(Constants.STRING_TYPE_INFO, "String");
        tempMap.put(Constants.VOID_TYPE_INFO, "!");
        tempMap.put(Constants.OBJECT_TYPE_INFO, "Any");
        typeNameMappings = Collections.unmodifiableMap(tempMap);
    }

    @Override
    public void buildTypeExpr(ConcreteTypeInfo typeInfo, StringBuilder exprBuilder) {
        exprBuilder.append(typeNameMappings.getOrDefault(typeInfo, typeInfo.simpleName()));
    }

    @Override
    public void buildArrayExprPrefix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {

    }

    @Override
    public void buildArrayExprSuffix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {

    }
}
