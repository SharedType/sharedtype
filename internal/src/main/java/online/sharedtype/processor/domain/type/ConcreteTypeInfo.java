package online.sharedtype.processor.domain.type;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.def.ConcreteTypeDef;
import online.sharedtype.processor.domain.MappableType;
import online.sharedtype.processor.domain.def.TypeDef;

import online.sharedtype.processor.support.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
@EqualsAndHashCode(of = {"qualifiedName", "typeArgs"}, callSuper = false)
@Builder(toBuilder = true)
public final class ConcreteTypeInfo extends ReferableTypeInfo implements MappableType {
    private static final long serialVersionUID = 6912267731376244613L;
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<TypeInfo> typeArgs = Collections.emptyList();

    @Getter @Builder.Default
    private final Kind kind = Kind.CLASS;

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
    private final Map<SharedType.TargetType, String> mappedNames = new EnumMap<>(SharedType.TargetType.class);


    public static ConcreteTypeInfo ofPredefined(String qualifiedName, String simpleName) {
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
    public String mappedName(@Nullable SharedType.TargetType targetType) {
        return targetType == null ? null : mappedNames.get(targetType);
    }

    @Override
    public void addMappedName(SharedType.TargetType targetType, String mappedName) {
        mappedNames.put(targetType, mappedName);
    }

    public String simpleName() {
        return simpleName;
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
        ENUM, CLASS
    }
}
