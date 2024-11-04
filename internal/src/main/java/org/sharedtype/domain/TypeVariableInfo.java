package org.sharedtype.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

/**
 * Represents a generic type variable.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode
@Builder
public final class TypeVariableInfo implements TypeInfo {
    private final String name;
    // TODO: support generic bounds

    public String name() {
        return name;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
