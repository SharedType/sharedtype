package org.sharedtype.processor.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public final class ArrayTypeInfo implements TypeInfo {
    private final TypeInfo component;

    @Override
    public String toString() {
        return component + "[]";
    }
}
