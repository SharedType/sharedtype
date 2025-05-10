package online.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"},
    typescriptEnumFormat = "const_enum",
    goEnumFormat = "struct"
)
@RequiredArgsConstructor
public enum EnumSize {
    SMALL(1), MEDIUM(2), LARGE(3);

    @SharedType.EnumValue
    private final int size;
}
