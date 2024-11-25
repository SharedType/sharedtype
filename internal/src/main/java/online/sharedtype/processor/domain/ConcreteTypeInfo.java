package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a primitive type or object type that requires its target representation,
 * and is not recognized as an array-like type.
 * Like {@link java.lang.String} in typescript as "string", int in typescript as "number".
 *
 * @see ArrayTypeInfo
 * @author Cause Chung
 */
@EqualsAndHashCode(of = {"qualifiedName", "typeArgs"})
@Builder
public final class ConcreteTypeInfo implements TypeInfo {
    private static final long serialVersionUID = 6912267731376244613L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<? extends TypeInfo> typeArgs = Collections.emptyList();
    @Builder.Default
    private boolean resolved = true;

    /**
     * The corresponding type definition.
     * @see this#typeDef()
     */
    @Nullable
    private TypeDef typeDef;

    /**
     * The corresponding type variable if this type info is a reified type argument.
     */
    @Nullable
    private TypeVariableInfo typeVariable;

    static ConcreteTypeInfo ofPredefined(String qualifiedName, String simpleName) {
        return ConcreteTypeInfo.builder().qualifiedName(qualifiedName).simpleName(simpleName).build();
    }

    @Override
    public boolean resolved() {
        return resolved && typeArgs.stream().allMatch(TypeInfo::resolved);
    }

    public boolean shallowResolved() {
        return resolved;
    }

    public void markShallowResolved(TypeDef resolvedTypeDef) {
        this.resolved = true;
        this.typeDef = resolvedTypeDef;
    }

    /**
     * @return null when the type is not resolved if it's a user defined type; or does not have a corresponding {@link TypeDef}, e.g. a predefined type.
     */
    @Nullable
    public TypeDef typeDef() {
        return typeDef;
    }

    /**
     * @return null if this type info is not a reified type argument.
     */
    @Nullable
    public TypeVariableInfo reifiedTypeVariable() {
        return typeVariable;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

    public String simpleName() {
        return simpleName;
    }

    public List<? extends TypeInfo> typeArgs() {
        return typeArgs;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s",
                qualifiedName,
                typeArgs.isEmpty() ? "" : "<" + typeArgs.stream().map(TypeInfo::toString).collect(Collectors.joining(",")) + ">",
                resolved ? "" : "?"
        );
    }
}
