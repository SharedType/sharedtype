package online.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

@SharedType(rustMacroTraits = {"serde::Serialize", "serde::Deserialize"})
@RequiredArgsConstructor
public final class DependencyClassA extends SuperClassA {
    private final DependencyClassB b;
}
