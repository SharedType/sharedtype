package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.TypeInfo;

public interface TypeExpressionConverter {
    String toTypeExpr(TypeInfo typeInfo);

    static TypeExpressionConverter typescript(Context ctx) {
        return new TypescriptTypeExpressionConverter(ctx);
    }

    static TypeExpressionConverter rust() {
        return new RustTypeExpressionConverter();
    }
}
