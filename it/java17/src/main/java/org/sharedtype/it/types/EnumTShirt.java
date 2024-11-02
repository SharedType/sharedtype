package org.sharedtype.it.types;

import lombok.Getter;
import org.sharedtype.annotation.SharedType;

import static org.sharedtype.it.types.EnumSize.*;

@SharedType
@Getter
enum EnumTShirt {
    S(SMALL, "S"),
    M(MEDIUM, "M"),
    L(LARGE, "L"),
    ;

    private final EnumSize size;
    private final String name;

    EnumTShirt(EnumSize size, @SharedType.EnumValue String name) {
        this.size = size;
        this.name = name;
    }
}
