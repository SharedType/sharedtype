package online.sharedtype.it.java8;

import lombok.Data;
import lombok.EqualsAndHashCode;
import online.sharedtype.SharedType;

@SharedType(rustMacroTraits = {"PartialEq", "serde::Serialize", "serde::Deserialize"})
@Data
@EqualsAndHashCode(callSuper = true)
public final class DependencyClassA extends SuperClassA {
    private DependencyClassB b;
}
