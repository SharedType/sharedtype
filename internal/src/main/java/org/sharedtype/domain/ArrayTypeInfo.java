package org.sharedtype.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

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
