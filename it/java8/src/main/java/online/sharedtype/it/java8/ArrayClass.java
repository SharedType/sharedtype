package online.sharedtype.it.java8;

import lombok.Data;
import online.sharedtype.SharedType;

@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"}
)
@Data
public final class ArrayClass {
    private CustomList arr;
}
