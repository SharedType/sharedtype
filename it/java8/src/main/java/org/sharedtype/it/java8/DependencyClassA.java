package org.sharedtype.it.java8;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DependencyClassA extends SuperClassA{
    private final DependencyClassB b;
}
