package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;

public interface TypeExpressionConverter {
    String toTypeExpr(TypeInfo typeInfo, TypeDef contextTypeDef);

    static TypeExpressionConverter typescript(Context ctx) {
        return new TypescriptTypeExpressionConverter(ctx);
    }

    static TypeExpressionConverter go(Context ctx) {
        return new GoTypeExpressionConverter(ctx);
    }

    static TypeExpressionConverter rust(Context ctx) {
        return new RustTypeExpressionConverter(ctx);
    }

    static TypeExpressionConverter rustLiteral() {
        return new RustConstantLiteralTypeExpressionConverter();
    }

    @RequiredArgsConstructor
    final class ArraySpec {
        final String prefix;
        final String suffix;
    }

    @RequiredArgsConstructor
    final class MapSpec {
        final String prefix;
        final String delimiter;
        final String suffix;
    }

    @RequiredArgsConstructor
    final class TypeArgsSpec {
        final String prefix;
        final String delimiter;
        final String suffix;
    }
}
