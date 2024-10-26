package org.sharedtype.domain;

public record EnumValueInfo(TypeInfo type, Object value) implements ComponentInfo {
    @Override
    public boolean resolved() {
        return type.resolved();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
