package online.sharedtype.it.java8;

import lombok.Getter;
import lombok.Setter;
import online.sharedtype.SharedType;

@SharedType(rustMacroTraits = {"serde::Serialize", "serde::Deserialize"})
@Getter
@Setter
public final class DependencyClassC {
    private DependencyClassA a;
}
