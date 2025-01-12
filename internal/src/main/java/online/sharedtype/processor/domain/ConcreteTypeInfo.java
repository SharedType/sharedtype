package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
@Builder(toBuilder = true)
public final class ConcreteTypeInfo implements TypeInfo {
    private static final long serialVersionUID = 6912267731376244613L;
    private final String qualifiedName;
    private final String simpleName;
    /**
     * Where this typeInfo is depended. It can be a supertype, a field reference, or type parameter.
     * @see this#dependingKind
     */
    @Nullable
    private final String dependingTypeQualifiedName;
    @Nullable
    private final DependingKind dependingKind;
    @Builder.Default
    private final List<? extends TypeInfo> typeArgs = Collections.emptyList();
    @Builder.Default
    private boolean resolved = true;

    /**
     * The counter-parting type definition.
     * @see this#typeDef()
     */
    @Nullable
    private TypeDef typeDef;


    static ConcreteTypeInfo ofPredefined(String qualifiedName, String simpleName) {
        return ConcreteTypeInfo.builder().qualifiedName(qualifiedName).simpleName(simpleName).build();
    }

    @Override
    public boolean resolved() {
        return resolved && typeArgs.stream().allMatch(TypeInfo::resolved);
    }

    @Override
    public TypeInfo reify(Map<TypeVariableInfo, TypeInfo> mappings) {
        return this.toBuilder()
            .typeArgs(typeArgs.stream().map(typeArg -> typeArg.reify(mappings)).collect(Collectors.toList()))
            .build();
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

    public String qualifiedName() {
        return qualifiedName;
    }

    public String simpleName() {
        return simpleName;
    }

    @Nullable
    public String dependingTypeQualifiedName() {
        return dependingTypeQualifiedName;
    }
    @Nullable
    public DependingKind dependingKind() {
        return dependingKind;
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
