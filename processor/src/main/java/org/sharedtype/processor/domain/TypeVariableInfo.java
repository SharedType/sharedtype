package org.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Builder
public final class TypeVariableInfo implements TypeInfo {
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
