package online.sharedtype.it.java8;

import lombok.Getter;
import online.sharedtype.SharedType;

@SharedType(typescriptEnumFormat = "const_enum")
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
