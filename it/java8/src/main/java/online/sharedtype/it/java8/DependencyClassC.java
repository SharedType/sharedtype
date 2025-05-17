package online.sharedtype.it.java8;

import lombok.Data;
import online.sharedtype.SharedType;

@SharedType(rustMacroTraits = {"PartialEq", "serde::Serialize", "serde::Deserialize"})
@Data
public final class DependencyClassC {
    private DependencyClassA a;
}
