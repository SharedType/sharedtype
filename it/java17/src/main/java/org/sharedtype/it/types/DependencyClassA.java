package org.sharedtype.it.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DependencyClassA extends SuperClassA{
    private final DependencyClassB b;
}
