package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.Constants;

final class RustTypeExpressionConverter extends AbstractTypeExpressionConverter {
    RustTypeExpressionConverter() {
        addTypeMapping(Constants.BOOLEAN_TYPE_INFO, "bool");
        addTypeMapping(Constants.BYTE_TYPE_INFO, "i8");
        addTypeMapping(Constants.CHAR_TYPE_INFO, "char");
        addTypeMapping(Constants.DOUBLE_TYPE_INFO, "f64");
        addTypeMapping(Constants.FLOAT_TYPE_INFO, "f32");
        addTypeMapping(Constants.INT_TYPE_INFO, "i32");
        addTypeMapping(Constants.LONG_TYPE_INFO, "i64");
        addTypeMapping(Constants.SHORT_TYPE_INFO, "i16");

        addTypeMapping(Constants.BOXED_BOOLEAN_TYPE_INFO, "bool");
        addTypeMapping(Constants.BOXED_BYTE_TYPE_INFO, "i8");
        addTypeMapping(Constants.BOXED_CHAR_TYPE_INFO, "char");
        addTypeMapping(Constants.BOXED_DOUBLE_TYPE_INFO, "f64");
        addTypeMapping(Constants.BOXED_FLOAT_TYPE_INFO, "f32");
        addTypeMapping(Constants.BOXED_INT_TYPE_INFO, "i32");
        addTypeMapping(Constants.BOXED_LONG_TYPE_INFO, "i64");
        addTypeMapping(Constants.BOXED_SHORT_TYPE_INFO, "i16");

        addTypeMapping(Constants.STRING_TYPE_INFO, "String");
        addTypeMapping(Constants.VOID_TYPE_INFO, "!");
        addTypeMapping(Constants.OBJECT_TYPE_INFO, "Box<dyn Any>");
    }

    @Override
    public void buildArrayExprPrefix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {
        exprBuilder.append("Vec<");
    }

    @Override
    public void buildArrayExprSuffix(ArrayTypeInfo typeInfo, StringBuilder exprBuilder) {
        exprBuilder.append(">");
    }
}
