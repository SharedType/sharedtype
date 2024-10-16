package org.sharedtype.processor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ArrayTypeInfo implements TypeInfo {
    private final TypeInfo component;

    @Override
    public String toString() {
        return component + "[]";
    }
}
