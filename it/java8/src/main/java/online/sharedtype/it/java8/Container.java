package online.sharedtype.it.java8;

import lombok.Data;
import online.sharedtype.SharedType;

@SharedType(
    rustMacroTraits = {"serde::Serialize", "serde::Deserialize"}
)
@Data
public final class Container<T> {
    private T t;
}
