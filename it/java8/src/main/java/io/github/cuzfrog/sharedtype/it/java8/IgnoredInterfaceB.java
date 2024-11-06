package io.github.cuzfrog.sharedtype.it.java8;

import io.github.cuzfrog.sharedtype.SharedType;

@SharedType.Ignore
interface IgnoredInterfaceB {
    default boolean getBooleanValue() {
        return false;
    }

    int getNotIgnoredImplementedMethod();
}
