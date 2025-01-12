package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.SideEffect;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractTypeExpressionConverter implements TypeExpressionConverter {
    private final Map<ConcreteTypeInfo, String> typeNameMappings;

    public AbstractTypeExpressionConverter() {
        typeNameMappings = new HashMap<>(20);
    }

    @Override
    public final String toTypeExpr(TypeInfo typeInfo) {
        StringBuilder exprBuilder = new StringBuilder(); // TODO: a better init size
        buildTypeExprRecursively(typeInfo, exprBuilder);
        return exprBuilder.toString();
    }
    final void addTypeMapping(ConcreteTypeInfo typeInfo, String name) {
        typeNameMappings.put(typeInfo, name);
    }

    void buildArrayExprPrefix(ArrayTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder) {
    }
    void buildArrayExprSuffix(ArrayTypeInfo typeInfo, @SideEffect StringBuilder exprBuilder) {
    }
    void beforeVisitTypeInfo(TypeInfo typeInfo) {
    }
    String toConcreteTypeExpression(ConcreteTypeInfo concreteTypeInfo) {
        return concreteTypeInfo.simpleName();
    }

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder exprBuilder) {
        beforeVisitTypeInfo(typeInfo);
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            exprBuilder.append(typeNameMappings.getOrDefault(concreteTypeInfo, toConcreteTypeExpression(concreteTypeInfo)));
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
            buildArrayExprPrefix(arrayTypeInfo, exprBuilder);
            buildTypeExprRecursively(arrayTypeInfo.component(), exprBuilder);
            buildArrayExprSuffix(arrayTypeInfo, exprBuilder);
        }
    }
}
