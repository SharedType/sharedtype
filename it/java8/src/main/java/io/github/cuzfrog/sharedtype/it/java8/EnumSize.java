package io.github.cuzfrog.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;
import io.github.cuzfrog.sharedtype.SharedType;

@RequiredArgsConstructor
public enum EnumSize {
    SMALL(1), MEDIUM(2), LARGE(3);

    @SharedType.EnumValue
    private final int size;
}
