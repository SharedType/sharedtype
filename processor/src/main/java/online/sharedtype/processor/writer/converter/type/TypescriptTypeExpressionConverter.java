package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

final class TypescriptTypeExpressionConverter extends AbstractTypeExpressionConverter {
    final Map<TypeInfo, String> typeNameMappings = new HashMap<>(20);

    TypescriptTypeExpressionConverter(Context ctx) {
        super(
            ctx,
            new ArraySpec("", "[]"),
            new MapSpec("Record<", ", ", ">")
        );
        typeNameMappings.put(Constants.BOOLEAN_TYPE_INFO, "boolean");
        typeNameMappings.put(Constants.BYTE_TYPE_INFO, "number");
        typeNameMappings.put(Constants.CHAR_TYPE_INFO, "string");
        typeNameMappings.put(Constants.DOUBLE_TYPE_INFO, "number");
        typeNameMappings.put(Constants.FLOAT_TYPE_INFO, "number");
        typeNameMappings.put(Constants.INT_TYPE_INFO, "number");
        typeNameMappings.put(Constants.LONG_TYPE_INFO, "number");
        typeNameMappings.put(Constants.SHORT_TYPE_INFO, "number");

        typeNameMappings.put(Constants.BOXED_BOOLEAN_TYPE_INFO, "boolean");
        typeNameMappings.put(Constants.BOXED_BYTE_TYPE_INFO, "number");
        typeNameMappings.put(Constants.BOXED_CHAR_TYPE_INFO, "string");
        typeNameMappings.put(Constants.BOXED_DOUBLE_TYPE_INFO, "number");
        typeNameMappings.put(Constants.BOXED_FLOAT_TYPE_INFO, "number");
        typeNameMappings.put(Constants.BOXED_INT_TYPE_INFO, "number");
        typeNameMappings.put(Constants.BOXED_LONG_TYPE_INFO, "number");
        typeNameMappings.put(Constants.BOXED_SHORT_TYPE_INFO, "number");

        typeNameMappings.put(Constants.STRING_TYPE_INFO, "string");
        typeNameMappings.put(Constants.VOID_TYPE_INFO, "never");
        typeNameMappings.put(Constants.OBJECT_TYPE_INFO, ctx.getProps().getTypescript().getJavaObjectMapType());
    }

    @Override
    @Nullable
    String toTypeExpression(ConcreteTypeInfo typeInfo, String defaultExpr) {
        return typeNameMappings.getOrDefault(typeInfo, defaultExpr);
    }
}
