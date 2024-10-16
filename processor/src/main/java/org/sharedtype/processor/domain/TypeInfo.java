package org.sharedtype.processor.domain;

public interface TypeInfo {
    default boolean resolved() {
        return true;
    }
}
