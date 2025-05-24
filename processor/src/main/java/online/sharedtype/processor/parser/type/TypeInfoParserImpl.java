package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.TypeStore;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.type.TypeVariableInfo;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static online.sharedtype.processor.domain.Constants.PRIMITIVES;
import static online.sharedtype.processor.support.Preconditions.checkArgument;

/**
 * @author Cause Chung
 */
final class TypeInfoParserImpl implements TypeInfoParser {
    private final Context ctx;
    private final TypeStore typeStore;
    private final Types types;

    TypeInfoParserImpl(Context ctx) {
        this.ctx = ctx;
        this.typeStore = ctx.getTypeStore();
        this.types = ctx.getProcessingEnv().getTypeUtils();
    }

    @Override
    public TypeInfo parse(TypeMirror typeMirror, TypeElement ctxTypeElement) {
        TypeKind typeKind = typeMirror.getKind();

        // TODO: use enumMap
        if (typeKind.isPrimitive()) {
            return PRIMITIVES.get(typeKind);
        } else if (typeKind == TypeKind.ARRAY) {
            return new ArrayTypeInfo(parse(((ArrayType) typeMirror).getComponentType(), ctxTypeElement));
        } else if (typeKind == TypeKind.DECLARED) {
            return parseDeclared((DeclaredType) typeMirror, ctxTypeElement);
        } else if (typeKind == TypeKind.TYPEVAR) {
            return parseTypeVariable((TypeVariable) typeMirror, ctxTypeElement);
        } else if (typeKind == TypeKind.EXECUTABLE) {
            return parse(((ExecutableType) typeMirror).getReturnType(), ctxTypeElement);
        } else if (typeKind == TypeKind.WILDCARD) {
            throw new SharedTypeException(String.format("Unsupported type: %s, typeKind: %s, contextType: %s." +
                    " SharedType currently does not support wildcard generic types." +
                    " If it's from a dependency type, consider ignore it via global properties.",
                typeMirror, typeKind, ctxTypeElement));
        } else if (typeKind == TypeKind.ERROR) {
            ctx.error(
                ctxTypeElement,
                "Failed to parse type '%s'. This is possibly because the type is not visible in the scope," +
                    " if JPMS is used to resolve dependencies, check if it is on module path.",
                typeMirror);
            return TypeInfo.NO_TYPE_INFO;
        }
        throw new SharedTypeInternalError(String.format("Unsupported type: %s, typeKind: %s, contextType: %s. " +
                "If it's from a dependency type, consider ignore it via global properties.",
            typeMirror, typeKind, ctxTypeElement));
    }

    private TypeInfo parseDeclared(DeclaredType declaredType, TypeElement ctxTypeElement) {
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        List<? extends TypeMirror> typeArgs = declaredType.getTypeArguments();

        int arrayStack = 0;
        TypeMirror currentType = declaredType;
        TypeInfo typeInfo = null;
        while (ctx.isArraylike(currentType)) {
            arrayStack++;
            currentType = locateArrayComponentType(currentType);
            if (currentType instanceof DeclaredType) {
                DeclaredType argDeclaredType = (DeclaredType) currentType;
                TypeElement element = (TypeElement) argDeclaredType.asElement();
                qualifiedName = element.getQualifiedName().toString();
                simpleName = element.getSimpleName().toString();
                typeArgs = argDeclaredType.getTypeArguments();
            } else if (currentType instanceof TypeVariable) {
                TypeVariable argTypeVariable = (TypeVariable) currentType;
                TypeVariableInfo typeVarInfo = parseTypeVariable(argTypeVariable, ctxTypeElement);
                qualifiedName = typeVarInfo.qualifiedName();
                simpleName = typeVarInfo.name();
                typeArgs = Collections.emptyList();
                typeInfo = typeVarInfo;
                break;
            }
        }

        List<TypeInfo> parsedTypeArgs = typeArgs.stream().map(typeArg -> parse(typeArg, ctxTypeElement)).collect(Collectors.toList());

        if (typeInfo == null) {
            typeInfo = typeStore.getTypeInfo(qualifiedName, parsedTypeArgs);
        }

        if (typeInfo == null && ctx.isDatetimelike(currentType)) {
            typeInfo = new DateTimeInfo(qualifiedName);
        }

        if (typeInfo == null) {
            boolean resolved = typeStore.containsTypeDef(qualifiedName) || ctx.isOptionalType(qualifiedName);
            typeInfo = ConcreteTypeInfo.builder()
                .qualifiedName(qualifiedName)
                .simpleName(simpleName)
                .typeArgs(parsedTypeArgs)
                .kind(parseKind(currentType))
                .baseMapType(ctx.getProps().getMaplikeTypeQualifiedNames().contains(qualifiedName))
                .resolved(resolved)
                .build();
            typeStore.saveTypeInfo(qualifiedName, parsedTypeArgs, typeInfo);
        }

        while (arrayStack > 0) {
            typeInfo = new ArrayTypeInfo(typeInfo);
            arrayStack--;
        }
        return typeInfo;
    }

    private ConcreteTypeInfo.Kind parseKind(TypeMirror typeMirror) {
        if (ctx.isMaplike(typeMirror)) {
            return ConcreteTypeInfo.Kind.MAP;
        } else if (ctx.isEnumType(typeMirror)) {
            return ConcreteTypeInfo.Kind.ENUM;
        } else {
            return ConcreteTypeInfo.Kind.OTHER;
        }
    }

    private TypeVariableInfo parseTypeVariable(TypeVariable typeVariable, TypeElement ctxTypeElement) {
        String contextTypeQualifiedName = ctxTypeElement.getQualifiedName().toString();
        String simpleName = typeVariable.asElement().getSimpleName().toString();
        String qualifiedName = TypeVariableInfo.concatQualifiedName(contextTypeQualifiedName, simpleName);
        TypeInfo typeInfo = typeStore.getTypeInfo(qualifiedName, Collections.emptyList());
        if (typeInfo != null) {
            return (TypeVariableInfo) typeInfo;
        }
        typeInfo = TypeVariableInfo.builder()
            .contextTypeQualifiedName(contextTypeQualifiedName)
            .name(simpleName)
            .qualifiedName(qualifiedName)
            .build();
        typeStore.saveTypeInfo(qualifiedName, Collections.emptyList(), typeInfo);
        return (TypeVariableInfo) typeInfo;
    }

    private TypeMirror locateArrayComponentType(TypeMirror typeMirror) {
        TypeMirror cur = typeMirror;
        int depth = 0;
        while (!ctx.isTopArrayType(cur)) {
            for (TypeMirror supertype : types.directSupertypes(cur)) {
                if (ctx.isArraylike(supertype)) {
                    cur = supertype;
                    break;
                }
            }
            if (depth++ > 100) {
                throw new SharedTypeInternalError("Array type hierarchy exceed max depth: " + typeMirror);
            }
        }
        List<? extends TypeMirror> typeArgs = ((DeclaredType)cur).getTypeArguments();
        checkArgument(typeArgs.size() == 1, "Array type must have exactly one type argument, but got: %s, type: %s", typeArgs.size(), typeMirror);
        return typeArgs.get(0);
    }
}
