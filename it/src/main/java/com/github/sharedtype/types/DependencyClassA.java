package com.github.sharedtype.types;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class DependencyClassA {
    private final DependencyClassB b;
}
