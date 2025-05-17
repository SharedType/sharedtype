package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;

import javax.annotation.Nullable;

final class RustTypeExpressionConverter extends AbstractTypeExpressionConverter {
    private static final ArraySpec ARRAY_SPEC = new ArraySpec("Vec<", ">");
    private static final MapSpec DEFAULT_MAP_SPEC = new MapSpec("HashMap<", ", ", ">");
    private final RenderFlags renderFlags;

    RustTypeExpressionConverter(Context ctx) {
        super(ctx);
        this.renderFlags = ctx.getRenderFlags();
    }

    @Override
    void beforeVisitTypeInfo(TypeInfo typeInfo) {
        if (typeInfo.equals(Constants.OBJECT_TYPE_INFO)) {
            renderFlags.setUseRustAny(true);
        } else if (typeInfo instanceof ConcreteTypeInfo && ((ConcreteTypeInfo) typeInfo).getKind() == ConcreteTypeInfo.Kind.MAP) {
            renderFlags.setUseRustMap(true);
        }
    }

    @Override
    ArraySpec arraySpec() {
        return ARRAY_SPEC;
    }

    @Override
    MapSpec mapSpec(ConcreteTypeInfo typeInfo) {
        return DEFAULT_MAP_SPEC;
    }

    @Override
    String dateTimeTypeExpr(DateTimeInfo dateTimeInfo, Config config) {
        return dateTimeInfo.mappedNameOrDefault(SharedType.TargetType.RUST, config.getRustTargetDatetimeTypeLiteral());
    }

    @Override
    @Nullable
    String toTypeExpression(ConcreteTypeInfo typeInfo, @Nullable String defaultExpr) {
        String expr = typeInfo.mappedName(SharedType.TargetType.RUST);
        if (expr == null) {
            expr = RustTypeNameMappings.getOrDefault(typeInfo, defaultExpr);
        }
        if (expr != null) {
            if (typeInfo.typeDef() != null && typeInfo.typeDef().isCyclicReferenced()) {
                expr = String.format("Box<%s>", expr);
            }
        }
        return expr;
    }
}
