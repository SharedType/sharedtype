package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.SideEffect;
import online.sharedtype.processor.support.exception.SharedTypeException;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

import static online.sharedtype.processor.support.Preconditions.requireNonNull;

@RequiredArgsConstructor
abstract class AbstractTypeExpressionConverter implements TypeExpressionConverter {
    final Context ctx;
    private final ArraySpec arraySpec;
    private final MapSpec mapSpec;

    @Override
    public final String toTypeExpr(TypeInfo typeInfo) {
        StringBuilder exprBuilder = new StringBuilder(); // TODO: a better init size
        buildTypeExprRecursively(typeInfo, exprBuilder);
        return exprBuilder.toString();
    }

    void beforeVisitTypeInfo(TypeInfo typeInfo) {
    }

    @Nullable
    abstract String toTypeExpression(TypeInfo typeInfo, @Nullable String fallback);

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder exprBuilder) {
        beforeVisitTypeInfo(typeInfo);
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            if (concreteTypeInfo.isMapType()) {
                buildMapType(concreteTypeInfo, exprBuilder);
            } else {
                exprBuilder.append(toTypeExpression(concreteTypeInfo, concreteTypeInfo.simpleName()));
                if (!concreteTypeInfo.typeArgs().isEmpty()) {
                    exprBuilder.append("<");
                    for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                        buildTypeExprRecursively(typeArg, exprBuilder);
                        exprBuilder.append(", ");
                    }
                    exprBuilder.setLength(exprBuilder.length() - 2);
                    exprBuilder.append(">");
                }
            }
        } else if (typeInfo instanceof TypeVariableInfo) {
            TypeVariableInfo typeVariableInfo = (TypeVariableInfo) typeInfo;
            exprBuilder.append(typeVariableInfo.name());
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            exprBuilder.append(arraySpec.prefix);
            buildTypeExprRecursively(arrayTypeInfo.component(), exprBuilder);
            exprBuilder.append(arraySpec.suffix);
        }
    }

    private void buildMapType(ConcreteTypeInfo concreteTypeInfo, @SideEffect StringBuilder exprBuilder) {
        if (mapSpec == null) {
            return;
        }

        ConcreteTypeInfo baseMapType = findBaseMapType(concreteTypeInfo);
        TypeInfo keyType = baseMapType.typeArgs().get(0);
        if (keyType.isEnumType()) {
            EnumDef enumDef = (EnumDef) requireNonNull(((ConcreteTypeInfo)keyType).typeDef(),
                "Key type of %s is an enum type, which must have a type definition. When trying to build expression for concrete type: %s",
                baseMapType.qualifiedName(), concreteTypeInfo);
            keyType = enumDef.components().isEmpty() ? Constants.STRING_TYPE_INFO : enumDef.components().get(0).type();
        }
        String keyTypeExpr = toTypeExpression(keyType, null);
        if (keyTypeExpr == null) {
            throw new SharedTypeException(String.format("Key type of %s must be string or numbers or enum, but here is %s. " +
                "When trying to build expression for concrete type: %s", baseMapType.qualifiedName(), keyType, concreteTypeInfo));
        }
        exprBuilder.append(mapSpec.keyPrefix);
        exprBuilder.append(keyTypeExpr);
        exprBuilder.append(mapSpec.keySuffix).append(mapSpec.delimiter).append(mapSpec.valuePrefix);
        buildTypeExprRecursively(baseMapType.typeArgs().get(1), exprBuilder);
        exprBuilder.append(mapSpec.valueSuffix);
    }

    private ConcreteTypeInfo findBaseMapType(ConcreteTypeInfo concreteTypeInfo) {
        Queue<ConcreteTypeInfo> queue = new ArrayDeque<>();
        ConcreteTypeInfo baseMapType = concreteTypeInfo;
        while (!ctx.getProps().getMaplikeTypeQualifiedNames().contains(baseMapType.qualifiedName())) {
            ClassDef typeDef = (ClassDef)requireNonNull(baseMapType.typeDef(), "Custom Map type must have a type definition, concrete type: %s", concreteTypeInfo);
            typeDef = typeDef.reify(baseMapType.typeArgs());
            for (TypeInfo supertype : typeDef.directSupertypes()) {
                if (supertype instanceof ConcreteTypeInfo) {
                    queue.add((ConcreteTypeInfo) supertype);
                }
            }
            baseMapType = requireNonNull(queue.poll(), "Cannot find a qualified type name of a map-like type, concrete type: %s", concreteTypeInfo);
        }
        if (baseMapType.typeArgs().size() != 2) {
            throw new SharedTypeException(String.format("Base Map type must have 2 type arguments, with first as the key type and the second as the value type," +
                "but here is %s. When trying to build expression for concrete type: %s", baseMapType, concreteTypeInfo));
        }
        return baseMapType;
    }
}
