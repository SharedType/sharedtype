package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.Constants;

final class TypescriptTypeExpressionConverter extends AbstractTypeExpressionConverter {
    TypescriptTypeExpressionConverter(Context ctx) {
        addTypeMapping(Constants.BOOLEAN_TYPE_INFO, "boolean");
        addTypeMapping(Constants.BYTE_TYPE_INFO, "number");
        addTypeMapping(Constants.CHAR_TYPE_INFO, "string");
        addTypeMapping(Constants.DOUBLE_TYPE_INFO, "number");
        addTypeMapping(Constants.FLOAT_TYPE_INFO, "number");
        addTypeMapping(Constants.INT_TYPE_INFO, "number");
        addTypeMapping(Constants.LONG_TYPE_INFO, "number");
        addTypeMapping(Constants.SHORT_TYPE_INFO, "number");

        addTypeMapping(Constants.BOXED_BOOLEAN_TYPE_INFO, "boolean");
        addTypeMapping(Constants.BOXED_BYTE_TYPE_INFO, "number");
        addTypeMapping(Constants.BOXED_CHAR_TYPE_INFO, "string");
        addTypeMapping(Constants.BOXED_DOUBLE_TYPE_INFO, "number");
        addTypeMapping(Constants.BOXED_FLOAT_TYPE_INFO, "number");
        addTypeMapping(Constants.BOXED_INT_TYPE_INFO, "number");
        addTypeMapping(Constants.BOXED_LONG_TYPE_INFO, "number");
        addTypeMapping(Constants.BOXED_SHORT_TYPE_INFO, "number");

        addTypeMapping(Constants.STRING_TYPE_INFO, "string");
        addTypeMapping(Constants.VOID_TYPE_INFO, "never");
        addTypeMapping(Constants.OBJECT_TYPE_INFO, ctx.getProps().getTypescript().getJavaObjectMapType());
    }

    @Override
    void buildArrayExprSuffix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {
        exprBuilder.append("[]");
    }
}
