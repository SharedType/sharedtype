package online.sharedtype.processor.parser.type;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.context.Context;
import online.sharedtype.support.exception.SharedTypeInternalError;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static online.sharedtype.processor.domain.Constants.PRIMITIVES;
import static online.sharedtype.support.Preconditions.checkArgument;

/**
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
final class TypeInfoParserImpl implements TypeInfoParser {
    private final Context ctx;

    @Override
    public TypeInfo parse(TypeMirror typeMirror) {
        TypeKind typeKind = typeMirror.getKind();

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
        throw new SharedTypeInternalError(String.format("Unsupported type: %s, typeKind: %s", typeMirror, typeKind));
    }

    private TypeInfo parseDeclared(DeclaredType declaredType) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        List<? extends TypeMirror> typeArgs = declaredType.getTypeArguments();

        int arrayStack = 0;
        boolean isTypeVar = false;
        TypeMirror currentType = declaredType;
        while (ctx.isArraylike(currentType)) {
            checkArgument(typeArgs.size() == 1, "Array type must have exactly one type argument, but got: %s, type: %s", typeArgs.size(), currentType);
            arrayStack++;
            currentType = typeArgs.get(0);
            if (currentType instanceof DeclaredType) {
                DeclaredType argDeclaredType = (DeclaredType) currentType;
                TypeElement element = (TypeElement) argDeclaredType.asElement();
                qualifiedName = element.getQualifiedName().toString();
                simpleName = element.getSimpleName().toString();
                typeArgs = argDeclaredType.getTypeArguments();
            } else if (currentType instanceof TypeVariable) {
                TypeVariable argTypeVariable = (TypeVariable) currentType;
                TypeVariableInfo typeVarInfo = parseTypeVariable(argTypeVariable);
                qualifiedName = typeVarInfo.name();
                simpleName = typeVarInfo.name();
                typeArgs = Collections.emptyList();
                isTypeVar = true;
            }
        }
        /* This check should be enough since array types have been stripped off.
         *
         * Generic type with different reified type arguments have different literal representations.
         * E.g. List<String> and List<Integer> are different types.
         * In target code this could be e.g. interface A extends List<String> {} and interface B extends List<Integer> {}.
         * So generic types are not easy to compare in terms of caching. Current implementation does not cache generic types.
         */
        boolean isGeneric = !typeArgs.isEmpty();

        TypeInfo typeInfo = null;
        if (!isGeneric) {
            typeInfo = ctx.getTypeStore().getTypeInfo(qualifiedName);
        }

        if (typeInfo == null) {
            boolean resolved = isTypeVar || ctx.getTypeStore().contains(qualifiedName);
            List<TypeInfo> parsedTypeArgs = typeArgs.stream().map(this::parse).collect(Collectors.toList());
            typeInfo = ConcreteTypeInfo.builder()
                .qualifiedName(qualifiedName)
                .simpleName(simpleName)
                .typeArgs(parsedTypeArgs)
                .resolved(resolved)
                .build();

            if (!isGeneric) {
                ctx.getTypeStore().saveTypeInfo(qualifiedName, typeInfo);
            }
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
