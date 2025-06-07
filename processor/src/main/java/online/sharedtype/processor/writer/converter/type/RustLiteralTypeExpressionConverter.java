package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
final class RustLiteralTypeExpressionConverter implements TypeExpressionConverter {
    private static final Set<ConcreteTypeInfo> STRING_TYPES = new HashSet<>(3);
    static {
        STRING_TYPES.add(Constants.STRING_TYPE_INFO);
        STRING_TYPES.addAll(Constants.MATH_TYPES);
    }

    private final Context ctx;

    @Override
    public String toTypeExpr(TypeInfo typeInfo, TypeDef contextTypeDef) {
        if (!(typeInfo instanceof ConcreteTypeInfo)) {
            throw new SharedTypeInternalError(String.format("Literal types must be concrete types, but got: %s in %s", typeInfo, contextTypeDef));
        }
        ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
        if (concreteTypeInfo.getKind() == ConcreteTypeInfo.Kind.ENUM) {
            EnumDef enumDef = (EnumDef) concreteTypeInfo.typeDef();
            if (enumDef.hasComponentValueType() && ctx.getProps().getRust().hasEnumValueTypeAlias()) {
                return enumDef.valueTypeAlias();
            }
        }

        if (STRING_TYPES.contains(concreteTypeInfo)) {
            return "&'static str";
        }
        return RustTypeNameMappings.getOrDefault(concreteTypeInfo, concreteTypeInfo.simpleName());
    }
}
