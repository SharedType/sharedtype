package org.jets.processor.domain;

public interface TypeInfo {
    default boolean resolved() {
        return true;
    }

    default boolean isArray() {
        return false;
    }
}
