package org.sharedtype.processor.parser.field;

import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ConcreteTypeInfo;
import org.sharedtype.processor.domain.TypeInfo;
import org.sharedtype.processor.domain.TypeVariableInfo;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

@Singleton
final class TypescriptVariableElementParser implements VariableElementParser {
    private static final Map<TypeKind, ConcreteTypeInfo> PRIMITIVES = Map.of(
        TypeKind.BOOLEAN, ConcreteTypeInfo.ofPredefined("boolean", "boolean"),
        TypeKind.BYTE, ConcreteTypeInfo.ofPredefined("byte", "number"),
        TypeKind.CHAR, ConcreteTypeInfo.ofPredefined("char", "string"),
        TypeKind.DOUBLE, ConcreteTypeInfo.ofPredefined("double", "number"),
        TypeKind.FLOAT, ConcreteTypeInfo.ofPredefined("float", "number"),
        TypeKind.INT, ConcreteTypeInfo.ofPredefined("int", "number"),
        TypeKind.LONG, ConcreteTypeInfo.ofPredefined("long", "number"),
        TypeKind.SHORT, ConcreteTypeInfo.ofPredefined("short", "number")
    );
    private static final Map<String, ConcreteTypeInfo> PREDEFINED_OBJECT_TYPES = Map.of(
        "java.lang.Boolean", ConcreteTypeInfo.ofPredefined("java.lang.Boolean", "boolean"),
        "java.lang.Byte", ConcreteTypeInfo.ofPredefined("java.lang.Byte", "number"),
        "java.lang.Character", ConcreteTypeInfo.ofPredefined("java.lang.Character", "string"),
        "java.lang.Double", ConcreteTypeInfo.ofPredefined("java.lang.Double", "number"),
        "java.lang.Float", ConcreteTypeInfo.ofPredefined("java.lang.Float", "number"),
        "java.lang.Integer", ConcreteTypeInfo.ofPredefined("java.lang.Integer", "number"),
        "java.lang.Long", ConcreteTypeInfo.ofPredefined("java.lang.Long", "number"),
        "java.lang.Short", ConcreteTypeInfo.ofPredefined("java.lang.Short", "number"),
        "java.lang.String", ConcreteTypeInfo.ofPredefined("java.lang.String", "string"),
        "java.lang.Void", ConcreteTypeInfo.ofPredefined("java.lang.Void", "never")
    );

    private static final String OBJECT_NAME = Object.class.getName();
    private final Context ctx;
    private final Types types;
    private final Elements elements;
    private final Map<String, ConcreteTypeInfo> predefinedObjectTypes;

    @Inject
    TypescriptVariableElementParser(Context ctx) {
        this.ctx = ctx;
        this.types = ctx.getProcessingEnv().getTypeUtils();
        this.elements = ctx.getProcessingEnv().getElementUtils();
        this.predefinedObjectTypes = new HashMap<>(PREDEFINED_OBJECT_TYPES);
        predefinedObjectTypes.put(OBJECT_NAME, ConcreteTypeInfo.ofPredefined(OBJECT_NAME, ctx.getProps().getJavaObjectMapType()));
        predefinedObjectTypes.forEach((qualifiedName, typeInfo) -> ctx.saveType(qualifiedName, typeInfo.simpleName()));
    }

    @Override
    public TypeInfo parse(VariableElement element) {
        var typeMirror = element.asType();
        var typeKind = typeMirror.getKind();

        if (typeKind.isPrimitive()) {
            return PRIMITIVES.get(typeKind);
        } else if (typeKind == TypeKind.ARRAY) {
            throw new UnsupportedOperationException("Not implemented:" + typeMirror);
        } else if (typeKind == TypeKind.DECLARED) {
            return parseDeclared((DeclaredType) typeMirror);
        } else if (typeKind == TypeKind.TYPEVAR) {
            return parseTypeVariable((TypeVariable) typeMirror);
        }
        throw new SharedTypeInternalError(String.format("Unsupported field type, element: %s, typeKind: %s", element, typeKind)); // TODO: context info
    }

    private TypeInfo parseDeclared(DeclaredType declaredType) {
        var typeElement = (TypeElement)declaredType.asElement();

        var qualifiedName = typeElement.getQualifiedName().toString();
        var predefinedTypeInfo = predefinedObjectTypes.get(qualifiedName);
        var typeArgs = ctx.getExtraUtils().getTypeArguments(declaredType);
        if (predefinedTypeInfo != null) {
            return predefinedTypeInfo;
        }

        boolean isArray = false;
        if (ctx.getExtraUtils().isArraylike(declaredType)) {
            ctx.checkArgument(typeArgs.size() == 1,
                    "Array type must have exactly one type argument, but got: %s, type: %s", typeArgs.size(), typeElement);
            var typeArg = typeArgs.get(0);
            var element = types.asElement(typeArg);
            if (element instanceof TypeElement argTypeElement) {
                qualifiedName = argTypeElement.getQualifiedName().toString();
                isArray = true;
                typeArgs = ctx.getExtraUtils().getTypeArguments(typeArg);
            } else {
                throw new UnsupportedOperationException(String.format("Type: %s, typeArg: %s", declaredType, typeArg));
            }
        }

        var parsedTypeArgs = typeArgs.stream().map(this::parseDeclared).toList();

        var resolved = ctx.hasType(qualifiedName);
        return ConcreteTypeInfo.builder()
                .qualifiedName(qualifiedName)
                .simpleName(ctx.getSimpleName(qualifiedName))
                .array(isArray)
                .typeArgs(parsedTypeArgs)
                .resolved(resolved)
                .build();
    }

    private TypeInfo parseTypeVariable(TypeVariable typeVariable) {
        return TypeVariableInfo.builder()
                .name(typeVariable.asElement().getSimpleName().toString())
                .build();
    }
}
