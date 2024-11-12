package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.OutputTarget;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

public interface TypeExpressionConverter {
    String toTypeExpr(TypeInfo typeInfo);

    static TypeExpressionConverter create(OutputTarget target, Context ctx) {
        if (target == OutputTarget.TYPESCRIPT) {
            return new RecursiveTypeExpressionConverter(new TypescriptTypeExpressionBuilder(ctx));
        }
        if (target == OutputTarget.RUST) {
            return new RecursiveTypeExpressionConverter(new RustTypeExpressionBuilder());
        }
        throw new SharedTypeInternalError(String.format("Unsupported target: %s", target));
    }
}
