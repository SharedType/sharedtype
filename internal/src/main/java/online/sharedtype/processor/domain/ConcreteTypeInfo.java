package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @Builder.Default
    private final List<TypeInfo> typeArgs = Collections.emptyList();

    @Getter
    private final Kind kind;

    /** If this type is defined in global config as base Map type */
    @Getter
    private final boolean baseMapType;

    /**
     * Qualified names of types from where this typeInfo is strongly referenced, i.e. as a component type.
     */
    @Builder.Default
    private final Set<TypeDef> referencingTypes = new HashSet<>();
    @Builder.Default
    private boolean resolved = true;

    /**
     * The counter-parting type definition.
     * @see #typeDef()
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
        if (resolvedTypeDef instanceof ConcreteTypeDef) {
            ((ConcreteTypeDef) resolvedTypeDef).linkTypeInfo(this);
        }
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

    public Set<TypeDef> referencingTypes() {
        return referencingTypes;
    }

    public List<TypeInfo> typeArgs() {
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

    /** Kind of types ConcreteTypeInfo can represent. Array is represented by ArrayTypeInfo */
    public enum Kind {
        ENUM, MAP, DATE_TIME, OTHER
    }
}
