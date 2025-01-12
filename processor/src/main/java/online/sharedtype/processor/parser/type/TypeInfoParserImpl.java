package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.TypeStore;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.TypeInfo;
import online.sharedtype.processor.domain.TypeVariableInfo;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

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
import static online.sharedtype.processor.support.Preconditions.checkArgument;

/**
 *
 * @author Cause Chung
 */
final class TypeInfoParserImpl implements TypeInfoParser {
    private final Context ctx;
    private final TypeStore typeStore;

    TypeInfoParserImpl(Context ctx) {
        this.ctx = ctx;
        this.typeStore = ctx.getTypeStore();
    }

    @Override
    public TypeInfo parse(TypeMirror typeMirror, TypeContext typeContext) {
        TypeKind typeKind = typeMirror.getKind();

        // TODO: use enumMap
        if (typeKind.isPrimitive()) {
            return PRIMITIVES.get(typeKind);
        } else if (typeKind == TypeKind.ARRAY) {
            return new ArrayTypeInfo(parse(((ArrayType) typeMirror).getComponentType(), typeContext));
        } else if (typeKind == TypeKind.DECLARED) {
            return parseDeclared((DeclaredType) typeMirror, typeContext);
        } else if (typeKind == TypeKind.TYPEVAR) {
            return parseTypeVariable((TypeVariable) typeMirror, typeContext);
        } else if (typeKind == TypeKind.EXECUTABLE) {
            return parse(((ExecutableType) typeMirror).getReturnType(), typeContext);
        }
        throw new SharedTypeInternalError(String.format("Unsupported type: %s, typeKind: %s", typeMirror, typeKind));
    }

    private TypeInfo parseDeclared(DeclaredType declaredType, TypeContext typeContext) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        List<? extends TypeMirror> typeArgs = declaredType.getTypeArguments();

        int arrayStack = 0;
        TypeMirror currentType = declaredType;
        TypeInfo typeInfo = null;
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
                TypeVariableInfo typeVarInfo = parseTypeVariable(argTypeVariable, typeContext);
                qualifiedName = typeVarInfo.qualifiedName();
                simpleName = typeVarInfo.name();
                typeArgs = Collections.emptyList();
                typeInfo = typeVarInfo;
                break;
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

        if (typeInfo == null && !isGeneric) {
            typeInfo = typeStore.getTypeInfo(qualifiedName);
        }

        if (typeInfo == null) {
            boolean resolved = typeStore.contains(qualifiedName);
            List<TypeInfo> parsedTypeArgs = typeArgs.stream().map(typeArg -> parse(typeArg, typeContext)).collect(Collectors.toList());
            typeInfo = ConcreteTypeInfo.builder()
                .qualifiedName(qualifiedName)
                .simpleName(simpleName)
                .dependingTypeQualifiedName(typeContext.getQualifiedName()).dependingKind(typeContext.getDependingKind())
                .typeArgs(parsedTypeArgs)
                .resolved(resolved)
                .build();

            if (!isGeneric) {
                typeStore.saveTypeInfo(qualifiedName, typeInfo);
            }
        }

        while (arrayStack > 0) {
            typeInfo = new ArrayTypeInfo(typeInfo);
            arrayStack--;
        }
        return typeInfo;
    }

    private TypeVariableInfo parseTypeVariable(TypeVariable typeVariable, TypeContext typeContext) {
        String simpleName = typeVariable.asElement().getSimpleName().toString();
        String qualifiedName = TypeVariableInfo.concatQualifiedName(typeContext.getQualifiedName(), simpleName);
        TypeInfo typeInfo = typeStore.getTypeInfo(qualifiedName);
        if (typeInfo != null) {
            return (TypeVariableInfo)typeInfo;
        }
        typeInfo = TypeVariableInfo.builder()
            .contextTypeQualifiedName(typeContext.getQualifiedName())
            .name(simpleName)
            .qualifiedName(qualifiedName)
            .build();
        typeStore.saveTypeInfo(qualifiedName, typeInfo);
        return (TypeVariableInfo)typeInfo;
    }
}
