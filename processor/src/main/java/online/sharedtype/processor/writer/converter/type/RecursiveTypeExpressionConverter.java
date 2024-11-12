package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.Recursive;
import online.sharedtype.processor.support.annotation.SideEffect;

@RequiredArgsConstructor
final class RecursiveTypeExpressionConverter implements TypeExpressionConverter {
    private final TypeExpressionBuilder expressionBuilder;

    @Override
    public String toTypeExpr(TypeInfo typeInfo) {
        StringBuilder exprBuilder = new StringBuilder();
        buildTypeExprRecursively(typeInfo, exprBuilder);
        return exprBuilder.toString();
    }

    @Recursive
    private void buildTypeExprRecursively(TypeInfo typeInfo,
                                          @SideEffect StringBuilder exprBuilder) {
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            expressionBuilder.buildTypeExpr(concreteTypeInfo, exprBuilder);
            if (!concreteTypeInfo.typeArgs().isEmpty()) {
                exprBuilder.append("<");
                for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                    buildTypeExprRecursively(typeArg, exprBuilder);
                    exprBuilder.append(", ");
                }
                exprBuilder.setLength(exprBuilder.length() - 2);
                exprBuilder.append(">");
            }
        } else if (typeInfo instanceof TypeVariableInfo) {
            TypeVariableInfo typeVariableInfo = (TypeVariableInfo) typeInfo;
            exprBuilder.append(typeVariableInfo.name());
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            expressionBuilder.buildArrayExprPrefix(arrayTypeInfo, exprBuilder);
            buildTypeExprRecursively(arrayTypeInfo.component(), exprBuilder);
            expressionBuilder.buildArrayExprSuffix(arrayTypeInfo, exprBuilder);
        }
    }
}
