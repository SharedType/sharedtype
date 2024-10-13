package org.jets.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "qualifiedName")
@Builder
public final class TypeInfo {
    private final String qualifiedName;
    private String simpleName;
    @Builder.Default
    private final List<? extends TypeInfo> typeArgs = new ArrayList<>();
    @Builder.Default
    private boolean resolved = true;
    /** When is array, the type stands for array component type */
    @Builder.Default @Getter
    private final boolean array = false;

    public static TypeInfo ofPredefined(String qualifiedName, String simpleName) {
        return TypeInfo.builder().qualifiedName(qualifiedName).simpleName(simpleName).build();
    }

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

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public List<? extends TypeInfo> typeArgs() {
        return typeArgs;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s",
                qualifiedName,
                typeArgs.isEmpty() ? "" : "<" + String.join(",", typeArgs.stream().map(TypeInfo::qualifiedName).toList()) + ">",
                resolved ? "" : "?"
        );
    }
}
