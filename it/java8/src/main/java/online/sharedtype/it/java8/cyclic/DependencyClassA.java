package online.sharedtype.it.java8.cyclic;

import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.it.java8.SuperClassA;

@SharedType
@RequiredArgsConstructor
public final class DependencyClassA extends SuperClassA {
    private final DependencyClassB b;
}
