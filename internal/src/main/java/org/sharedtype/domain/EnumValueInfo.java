package org.sharedtype.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public final class EnumValueInfo implements ComponentInfo {
    private final TypeInfo type;
    private final Object value;

    public TypeInfo type() {
        return type;
    }

    public Object value() {
        return value;
    }

    @Override
    public boolean resolved() {
        return type.resolved();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
