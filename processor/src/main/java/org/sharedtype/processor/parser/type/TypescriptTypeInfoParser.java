package org.sharedtype.processor.parser.type;

import org.sharedtype.processor.context.Context;
import org.sharedtype.processor.domain.ArrayTypeInfo;
import org.sharedtype.processor.domain.ConcreteTypeInfo;
import org.sharedtype.processor.domain.TypeInfo;
import org.sharedtype.processor.domain.TypeVariableInfo;
import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.sharedtype.processor.support.Preconditions.checkArgument;

@Singleton
final class TypescriptTypeInfoParser implements TypeInfoParser {
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
    private final Map<String, ConcreteTypeInfo> predefinedObjectTypes;

    @Inject
    TypescriptTypeInfoParser(Context ctx) {
        this.ctx = ctx;
        this.predefinedObjectTypes = new HashMap<>(PREDEFINED_OBJECT_TYPES);
        predefinedObjectTypes.put(OBJECT_NAME, ConcreteTypeInfo.ofPredefined(OBJECT_NAME, ctx.getProps().getJavaObjectMapType()));
        predefinedObjectTypes.forEach((qualifiedName, typeInfo) -> ctx.saveType(qualifiedName, typeInfo.simpleName()));
    }

    @Override
    public TypeInfo parse(TypeMirror typeMirror) {
        var typeKind = typeMirror.getKind();

        // TODO: use enumMap
        if (typeKind.isPrimitive()) {
            return PRIMITIVES.get(typeKind);
        } else if (typeKind == TypeKind.ARRAY) {
            return new ArrayTypeInfo(parse(((ArrayType) typeMirror).getComponentType()));
        } else if (typeKind == TypeKind.DECLARED) {
            return parseDeclared((DeclaredType) typeMirror);
        } else if (typeKind == TypeKind.TYPEVAR) {
            return parseTypeVariable((TypeVariable) typeMirror);
        } else if (typeKind == TypeKind.EXECUTABLE) {
            return parse(((ExecutableType) typeMirror).getReturnType());
        }
        throw new SharedTypeInternalError(String.format("Unsupported field type, element: %s, typeKind: %s", typeMirror, typeKind)); // TODO: context info
    }

    private TypeInfo parseDeclared(DeclaredType declaredType) {
        var typeElement = (TypeElement)declaredType.asElement();
        var qualifiedName = typeElement.getQualifiedName().toString();
        var typeArgs = declaredType.getTypeArguments();

        int arrayStack = 0;
        boolean isTypeVar = false;
        TypeMirror currentType = declaredType;
        while (ctx.isArraylike(currentType)) {
            checkArgument(typeArgs.size() == 1, "Array type must have exactly one type argument, but got: %s, type: %s", typeArgs.size(), currentType);
            arrayStack++;
            currentType = typeArgs.get(0);
            if (currentType instanceof DeclaredType argDeclaredType) {
                var element = (TypeElement)argDeclaredType.asElement();
                qualifiedName = element.getQualifiedName().toString();
                typeArgs = argDeclaredType.getTypeArguments();
            } else if (currentType instanceof TypeVariable argTypeVariable) {
                var typeVarInfo = parseTypeVariable(argTypeVariable);
                qualifiedName = typeVarInfo.getName();
                typeArgs = Collections.emptyList();
                isTypeVar = true;
            }
        }

        TypeInfo typeInfo;
        var predefinedTypeInfo = predefinedObjectTypes.get(qualifiedName);
        if (predefinedTypeInfo != null) {
            typeInfo = predefinedTypeInfo;
        } else {
            var resolved = isTypeVar ||ctx.hasType(qualifiedName);
            var parsedTypeArgs = typeArgs.stream().map(this::parse).toList();
            typeInfo = ConcreteTypeInfo.builder()
              .qualifiedName(qualifiedName)
              .simpleName(ctx.getSimpleName(qualifiedName))
              .typeArgs(parsedTypeArgs)
              .resolved(resolved)
              .build();
        }

        while (arrayStack > 0) {
            typeInfo = new ArrayTypeInfo(typeInfo);
            arrayStack--;
        }
        return typeInfo;
    }

    private TypeVariableInfo parseTypeVariable(TypeVariable typeVariable) {
        return TypeVariableInfo.builder()
                .name(typeVariable.asElement().getSimpleName().toString())
                .build();
    }

}
