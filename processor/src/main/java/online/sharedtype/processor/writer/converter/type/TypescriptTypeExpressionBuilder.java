package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.support.annotation.SideEffect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class TypescriptTypeExpressionBuilder implements TypeExpressionBuilder {
    private final Map<ConcreteTypeInfo, String> typeNameMappings;

    TypescriptTypeExpressionBuilder(Context ctx) {
        Map<ConcreteTypeInfo, String> tempMap = new HashMap<>(20);
        tempMap.put(Constants.BOOLEAN_TYPE_INFO, "boolean");
        tempMap.put(Constants.BYTE_TYPE_INFO, "number");
        tempMap.put(Constants.CHAR_TYPE_INFO, "string");
        tempMap.put(Constants.DOUBLE_TYPE_INFO, "number");
        tempMap.put(Constants.FLOAT_TYPE_INFO, "number");
        tempMap.put(Constants.INT_TYPE_INFO, "number");
        tempMap.put(Constants.LONG_TYPE_INFO, "number");
        tempMap.put(Constants.SHORT_TYPE_INFO, "number");

        tempMap.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "boolean");
        tempMap.put(Constants.BOXED_BYTE_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_CHAR_TYPE_INFO, "string");
        tempMap.put(Constants.BOXED_DOUBLE_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_FLOAT_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_INT_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_LONG_TYPE_INFO, "number");
        tempMap.put(Constants.BOXED_SHORT_TYPE_INFO, "number");

        tempMap.put(Constants.STRING_TYPE_INFO, "string");
        tempMap.put(Constants.VOID_TYPE_INFO, "never");
        tempMap.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getTypescript().getJavaObjectMapType());

        typeNameMappings = Collections.unmodifiableMap(tempMap);
    }

    @Override
    public void buildTypeExpr(ConcreteTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder) {
        exprBuilder.append(typeNameMappings.getOrDefault(typeInfo, typeInfo.simpleName()));
    }

    @Override
    public void buildArrayExprPrefix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {
    }

    @Override
    public void buildArrayExprSuffix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {
        exprBuilder.append("[]");
    }
}
