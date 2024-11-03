package org.sharedtype.it.java8;

import lombok.Getter;
import org.sharedtype.annotation.SharedType;

@SharedType
@Getter
public enum EnumTShirt {
    S(EnumSize.SMALL, "S"),
    M(EnumSize.MEDIUM, "M"),
    L(EnumSize.LARGE, "L"),
    ;

    private final EnumSize size;
    private final String name;

    EnumTShirt(EnumSize size, @SharedType.EnumValue String name) {
        this.size = size;
        this.name = name;
    }
}
