package org.sharedtype.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Represents an array-like type.
 * During parsing, a predefined array-like type and its subtypes is captured as this class.
 * A type will be recognized as this type with higher priority than {@link ConcreteTypeInfo}.
 * <br>
 * Predefined array-like types can be configured in global properties. Default is {@link java.lang.Iterable}.
 *
 * @see ConcreteTypeInfo
 * @author Cause Chung
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class ArrayTypeInfo implements TypeInfo {
    private final TypeInfo component;

    public TypeInfo component() {
        return component;
    }

    @Override
    public boolean resolved() {
        return component.resolved();
    }

    @Override
    public String toString() {
        return component + "[]";
    }
}
