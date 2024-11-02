package org.sharedtype.it.types;

import lombok.RequiredArgsConstructor;
import org.sharedtype.annotation.SharedType;

@RequiredArgsConstructor
public enum EnumSize {
    SMALL(1), MEDIUM(2), LARGE(3);

    @SharedType.EnumValue
    private final int size;
}
