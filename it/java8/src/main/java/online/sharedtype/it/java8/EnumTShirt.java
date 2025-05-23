package online.sharedtype.it.java8;

import lombok.Getter;
import online.sharedtype.SharedType;

@SharedType(typescriptEnumFormat = "enum")
@Getter
public enum EnumTShirt {
    S(EnumSize.SMALL, "S"),
    M(EnumSize.MEDIUM, "M"),
    L(EnumSize.LARGE, "L"),
    ;

    private final EnumSize size;
    @SharedType.EnumValue
    private final String name;

    EnumTShirt(EnumSize size, String name) {
        this.size = size;
        this.name = name;
    }
}
