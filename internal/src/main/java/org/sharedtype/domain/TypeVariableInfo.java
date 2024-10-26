package org.sharedtype.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public final class TypeVariableInfo implements TypeInfo {
    private final String name;

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
