package org.sharedtype.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(of = "qualifiedName")
@Builder
public final class ConcreteTypeInfo implements TypeInfo {
    private final String qualifiedName;
    private final String simpleName;
    @Builder.Default
    private final List<? extends TypeInfo> typeArgs = Collections.emptyList();
    @Builder.Default
    private boolean resolved = true;

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

    public void markShallowResolved() {
        this.resolved = true;
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
