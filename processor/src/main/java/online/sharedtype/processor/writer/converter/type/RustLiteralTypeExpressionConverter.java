package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

final class RustLiteralTypeExpressionConverter implements TypeExpressionConverter {
    @Override
    public String toTypeExpr(TypeInfo typeInfo, TypeDef contextTypeDef) {
        if (!(typeInfo instanceof ConcreteTypeInfo)) {
            throw new SharedTypeInternalError(String.format("Literal types must be concrete types, but got: %s in %s", typeInfo, contextTypeDef));
        }
        ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
        if (concreteTypeInfo.equals(Constants.STRING_TYPE_INFO)) {
            return "&str";
        }
        return RustTypeNameMappings.getOrDefault(concreteTypeInfo, concreteTypeInfo.simpleName());
    }
}
