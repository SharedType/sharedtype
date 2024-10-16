package org.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(of = "qualifiedName")
@Builder
public final class ConcreteTypeInfo implements TypeInfo {
    private final String qualifiedName;
    @Setter
    private String simpleName;
    @Builder.Default
    private final List<? extends TypeInfo> typeArgs = Collections.emptyList();
    @Builder.Default
    private boolean resolved = true;

    public static ConcreteTypeInfo ofPredefined(String qualifiedName, String simpleName) {
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

    /**
     * A simple name stands for the type name used in output source code.
     * @return null when type not resolved or has no simple name, e.g. array type.
     */
    @Nullable
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
                typeArgs.isEmpty() ? "" : "<" + String.join(",", typeArgs.stream().map(TypeInfo::toString).toList()) + ">",
                resolved ? "" : "?"
        );
    }
}
