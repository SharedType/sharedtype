package online.sharedtype.processor.writer.converter.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.support.annotation.SideEffect;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import java.util.ArrayDeque;
import java.util.Queue;

import static online.sharedtype.processor.support.Preconditions.requireNonNull;

@RequiredArgsConstructor
abstract class AbstractTypeExpressionConverter implements TypeExpressionConverter {
    private static final int BUILDER_INIT_SIZE = 128; // TODO: better estimate
    private static final TypeArgsSpec DEFAULT_TYPE_ARGS_SPEC = new TypeArgsSpec("<", ", ", ">");
    final Context ctx;

    @Override
    public final String toTypeExpr(TypeInfo typeInfo, TypeDef contextTypeDef) {
        StringBuilder exprBuilder = new StringBuilder(BUILDER_INIT_SIZE);
        buildTypeExprRecursively(typeInfo, exprBuilder, contextTypeDef, ctx.getTypeStore().getConfig(contextTypeDef));
        return exprBuilder.toString();
    }

    void beforeVisitTypeInfo(TypeInfo typeInfo) {
    }

    abstract ArraySpec arraySpec();

    abstract MapSpec mapSpec(ConcreteTypeInfo typeInfo);

    TypeArgsSpec typeArgsSpec(ConcreteTypeInfo typeInfo) {
        return DEFAULT_TYPE_ARGS_SPEC;
    }

    abstract String dateTimeTypeExpr(DateTimeInfo dateTimeInfo, Config config);

    abstract String toTypeExpression(ConcreteTypeInfo typeInfo, String defaultExpr);

    private void buildTypeExprRecursively(TypeInfo typeInfo, @SideEffect StringBuilder exprBuilder, TypeDef contextTypeDef, Config config) {
        beforeVisitTypeInfo(typeInfo);
        if (typeInfo instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo concreteTypeInfo = (ConcreteTypeInfo) typeInfo;
            if (concreteTypeInfo.getKind() == ConcreteTypeInfo.Kind.MAP) {
                buildMapType(concreteTypeInfo, exprBuilder, contextTypeDef, config);
            } else {
                exprBuilder.append(toTypeExpression(concreteTypeInfo, concreteTypeInfo.simpleName()));
                if (!concreteTypeInfo.typeArgs().isEmpty()) {
                    TypeArgsSpec typeArgsSpec = typeArgsSpec(concreteTypeInfo);
                    exprBuilder.append(typeArgsSpec.prefix);
                    for (TypeInfo typeArg : concreteTypeInfo.typeArgs()) {
                        buildTypeExprRecursively(typeArg, exprBuilder, contextTypeDef, config);
                        exprBuilder.append(typeArgsSpec.delimiter);
                    }
                    exprBuilder.setLength(exprBuilder.length() - typeArgsSpec.delimiter.length());
                    exprBuilder.append(typeArgsSpec.suffix);
                }
            }
        } else if (typeInfo instanceof TypeVariableInfo) {
            TypeVariableInfo typeVariableInfo = (TypeVariableInfo) typeInfo;
            exprBuilder.append(typeVariableInfo.name());
        } else if (typeInfo instanceof ArrayTypeInfo) {
            ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) typeInfo;
            ArraySpec arraySpec = arraySpec();
            exprBuilder.append(arraySpec.prefix);
            buildTypeExprRecursively(arrayTypeInfo.component(), exprBuilder, contextTypeDef, config);
            exprBuilder.append(arraySpec.suffix);
        } else if (typeInfo instanceof DateTimeInfo) {
            exprBuilder.append(dateTimeTypeExpr((DateTimeInfo) typeInfo, config));
        }
    }

    private void buildMapType(ConcreteTypeInfo concreteTypeInfo,
                              @SideEffect StringBuilder exprBuilder,
                              TypeDef contextTypeDef,
                              Config config) {
        ConcreteTypeInfo baseMapType = findBaseMapType(concreteTypeInfo, contextTypeDef);
        ConcreteTypeInfo keyType = getKeyType(baseMapType, concreteTypeInfo, contextTypeDef);
        MapSpec mapSpec = mapSpec(keyType);
        if (mapSpec == null) {
            return;
        }
        String keyTypeExpr = toTypeExpression(keyType, keyType.simpleName());
        exprBuilder.append(mapSpec.prefix);
        exprBuilder.append(keyTypeExpr);
        exprBuilder.append(mapSpec.delimiter);
        buildTypeExprRecursively(baseMapType.typeArgs().get(1), exprBuilder, contextTypeDef, config);
        exprBuilder.append(mapSpec.suffix);
    }

    private ConcreteTypeInfo getKeyType(ConcreteTypeInfo baseMapType, ConcreteTypeInfo concreteTypeInfo, TypeDef contextTypeDef) {
        TypeInfo keyType = baseMapType.typeArgs().get(0);
        boolean validKey = false;
        if (keyType instanceof ConcreteTypeInfo && ((ConcreteTypeInfo) keyType).getKind() == ConcreteTypeInfo.Kind.ENUM) {
            validKey = true;
        } else if (Constants.LITERAL_TYPES.contains(keyType)) {
            validKey = true;
        }
        if (!validKey) {
            ctx.error(contextTypeDef.getElement(),
                "Key type of %s must be string or numbers or enum (with EnumValue being string or numbers), but is %s, " +
                    "when trying to build expression for concrete type: %s, context type: %s.",
                baseMapType.qualifiedName(), keyType, concreteTypeInfo, contextTypeDef);
        }
        if (!(keyType instanceof ConcreteTypeInfo)) {
            throw new SharedTypeInternalError(String.format(
                "Key type of %s is not a ConcreteTypeInfo, but is %s %s, " +
                    "when trying to build expression for concrete type: %s, context type: %s.",
                baseMapType.qualifiedName(), keyType.getClass(), keyType, concreteTypeInfo, contextTypeDef));
        }
        return (ConcreteTypeInfo) keyType;
    }

    private ConcreteTypeInfo findBaseMapType(ConcreteTypeInfo concreteTypeInfo, TypeDef contextTypeDef) {
        Queue<ConcreteTypeInfo> queue = new ArrayDeque<>();
        ConcreteTypeInfo baseMapType = concreteTypeInfo;
        while (!ctx.getProps().getMaplikeTypeQualifiedNames().contains(baseMapType.qualifiedName())) {
            ClassDef typeDef = (ClassDef) requireNonNull(baseMapType.typeDef(),
                "Custom Map type must have a type definition, concrete type: %s, current supertype: %s does not have a type definition.",
                concreteTypeInfo, baseMapType);
            typeDef = typeDef.reify(baseMapType.typeArgs());
            for (TypeInfo supertype : typeDef.directSupertypes()) {
                if (supertype instanceof ConcreteTypeInfo) {
                    queue.add((ConcreteTypeInfo) supertype);
                }
            }
            baseMapType = requireNonNull(queue.poll(), "Cannot find a qualified type name of a map-like type, concrete type: %s", concreteTypeInfo);
        }
        if (baseMapType.typeArgs().size() != 2) {
            ctx.error(contextTypeDef.getElement(), "Base Map type must have 2 type arguments, with first as the key type and the second as the value type," +
                "but is %s, when trying to build expression for concrete type: %s", baseMapType, concreteTypeInfo);
        }
        return baseMapType;
    }
}
