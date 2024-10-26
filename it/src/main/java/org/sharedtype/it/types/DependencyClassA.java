package org.sharedtype.it.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DependencyClassA {
    private final DependencyClassB b;
}
