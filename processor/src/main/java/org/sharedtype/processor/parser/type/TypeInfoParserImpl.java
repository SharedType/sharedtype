package org.sharedtype.processor.parser.type;

import lombok.RequiredArgsConstructor;
import org.sharedtype.domain.ArrayTypeInfo;
import org.sharedtype.domain.ConcreteTypeInfo;
import org.sharedtype.domain.TypeInfo;
import org.sharedtype.domain.TypeVariableInfo;
import org.sharedtype.processor.context.Context;
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

import static org.sharedtype.domain.Constants.PRIMITIVES;
import static org.sharedtype.processor.support.Preconditions.checkArgument;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Singleton
final class TypeInfoParserImpl implements TypeInfoParser {
    private final Context ctx;

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
        var typeElement = (TypeElement) declaredType.asElement();
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
                var element = (TypeElement) argDeclaredType.asElement();
                qualifiedName = element.getQualifiedName().toString();
                typeArgs = argDeclaredType.getTypeArguments();
            } else if (currentType instanceof TypeVariable argTypeVariable) {
                var typeVarInfo = parseTypeVariable(argTypeVariable);
                qualifiedName = typeVarInfo.getName();
                typeArgs = Collections.emptyList();
                isTypeVar = true;
            }
        }

        TypeInfo typeInfo = ctx.getTypeCache().getTypeInfo(qualifiedName);
        if (typeInfo == null) {
            var resolved = isTypeVar || ctx.getTypeCache().contains(qualifiedName);
            var parsedTypeArgs = typeArgs.stream().map(this::parse).toList();
            typeInfo = ConcreteTypeInfo.builder()
                .qualifiedName(qualifiedName)
                .typeArgs(parsedTypeArgs)
                .resolved(resolved)
                .build();
            ctx.getTypeCache().saveTypeInfo(qualifiedName, typeInfo);
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
