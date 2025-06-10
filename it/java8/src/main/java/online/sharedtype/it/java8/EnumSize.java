package online.sharedtype.it.java8;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"},
    typescriptEnumFormat = "const_enum",
    goEnumFormat = "struct"
)
@RequiredArgsConstructor
public enum EnumSize {
    SMALL(1),
    MEDIUM(2),
    @SharedType.TagLiteral(tags = "// test comments for enum")
    @SharedType.TagLiteral(tags = "// test inline comments", position = SharedType.TagPosition.INLINE_AFTER)
    LARGE(3);

    @JsonValue
    @SharedType.EnumValue
    private final int size;
}
