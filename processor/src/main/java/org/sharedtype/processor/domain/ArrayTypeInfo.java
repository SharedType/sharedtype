package org.sharedtype.processor.domain;

public record ArrayTypeInfo(TypeInfo component) implements TypeInfo {
    @Override
    public boolean resolved() {
        return component.resolved();
    }

    @Override
    public String toString() {
        return component + "[]";
    }
}
