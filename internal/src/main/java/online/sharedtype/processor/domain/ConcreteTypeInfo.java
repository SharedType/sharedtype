package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.SharedType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Represents a general primitive type or object type that requires its target representation.
 * Like {@link java.lang.String} in typescript as "string", int in typescript as "number".
 * </p>
 * <p>
 * Type with typical signature and render pattern like array-like types are represented as other type info.
 * </p>
 *
 * @author Cause Chung
 * @see ArrayTypeInfo
 * @see Kind
 */
@EqualsAndHashCode(of = {"qualifiedName", "typeArgs"})
@Builder(toBuilder = true)
public final class ConcreteTypeInfo implements TypeInfo, MappableType {
    private static final long serialVersionUID = 6912267731376244613L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<TypeInfo> typeArgs = Collections.emptyList();

    @Getter @Builder.Default
    private final Kind kind = Kind.OTHER;

    /**
     * If this type is defined in global config as base Map type
     */
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
     *
     * @see #typeDef()
     */
    @Nullable
    private TypeDef typeDef;

    /** Defined type mapping, see {@link SharedType} for details */
    private final Map<TargetCodeType, String> mappedNames = new EnumMap<>(TargetCodeType.class);


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

    @Nullable
    @Override
    public String mappedName(@Nullable TargetCodeType targetCodeType) {
        return targetCodeType == null ? null : mappedNames.get(targetCodeType);
    }

    @Override
    public void addMappedName(TargetCodeType targetCodeType, String mappedName) {
        mappedNames.put(targetCodeType, mappedName);
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

    /**
     * Kind of types ConcreteTypeInfo can represent.
     * Array is represented by ArrayTypeInfo.
     * Date/Time is represented by DateTimeTypeInfo.
     */
    public enum Kind {
        ENUM, MAP, OTHER
    }
}
