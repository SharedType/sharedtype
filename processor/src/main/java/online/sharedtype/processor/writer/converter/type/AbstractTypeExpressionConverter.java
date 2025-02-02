package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.SideEffect;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

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
    public final String toTypeExpr(TypeInfo typeInfo, TypeDef contextTypeDef) {
        StringBuilder exprBuilder = new StringBuilder(); // TODO: a better init size
        buildTypeExprRecursively(typeInfo, exprBuilder, contextTypeDef);
        return exprBuilder.toString();
    }

    void beforeVisitTypeInfo(TypeInfo typeInfo) {
    }

    @Nullable
    abstract String toTypeExpression(ConcreteTypeInfo typeInfo, @Nullable String defaultExpr);

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder exprBuilder, TypeDef contextTypeDef) {
        beforeVisitTypeInfo(typeInfo);
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            if (concreteTypeInfo.isMapType()) {
                buildMapType(concreteTypeInfo, exprBuilder, contextTypeDef);
            } else {
                exprBuilder.append(toTypeExpression(concreteTypeInfo, concreteTypeInfo.simpleName()));
                if (!concreteTypeInfo.typeArgs().isEmpty()) {
                    exprBuilder.append("<");
                    for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                        buildTypeExprRecursively(typeArg, exprBuilder, contextTypeDef);
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
            buildTypeExprRecursively(arrayTypeInfo.component(), exprBuilder, contextTypeDef);
            exprBuilder.append(arraySpec.suffix);
        }
    }

    private void buildMapType(ConcreteTypeInfo concreteTypeInfo, @SideEffect StringBuilder exprBuilder, TypeDef contextTypeDef) {
        if (mapSpec == null) {
            return;
        }

        ConcreteTypeInfo baseMapType = findBaseMapType(concreteTypeInfo);
        ConcreteTypeInfo keyType = getKeyType(baseMapType, concreteTypeInfo, contextTypeDef);
        String keyTypeExpr = toTypeExpression(keyType, keyType.simpleName());
        if (keyTypeExpr == null) {
            throw new SharedTypeInternalError(String.format(
                "Valid keyType should not be null, probably because type mapping is not provided, keyType: %s, baseMapType: %s" +
                "When trying to build expression for concrete type: %s. Context type: %s.", keyType, baseMapType, concreteTypeInfo, contextTypeDef));
        }
        exprBuilder.append(mapSpec.prefix);
        exprBuilder.append(keyTypeExpr);
        exprBuilder.append(mapSpec.delimiter);
        buildTypeExprRecursively(baseMapType.typeArgs().get(1), exprBuilder, contextTypeDef);
        exprBuilder.append(mapSpec.suffix);
    }

    private static ConcreteTypeInfo getKeyType(ConcreteTypeInfo baseMapType, ConcreteTypeInfo concreteTypeInfo, TypeDef contextTypeDef) {
        TypeInfo keyType = baseMapType.typeArgs().get(0);
        if (!(keyType instanceof ConcreteTypeInfo) || (!Constants.STRING_AND_NUMBER_TYPES.contains(keyType) && !keyType.isEnumType())) {
            throw new SharedTypeException(String.format(
                "Key type of %s must be string or numbers or enum (with EnumValue being string or numbers), but here is %s. " +
                "When trying to build expression for concrete type: %s. Context type: %s.",
                baseMapType.qualifiedName(), keyType, concreteTypeInfo, contextTypeDef));
        }
        return (ConcreteTypeInfo) keyType;
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
