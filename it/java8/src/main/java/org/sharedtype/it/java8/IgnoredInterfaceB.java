package org.sharedtype.it.java8;

import org.sharedtype.annotation.SharedType;

@SharedType.Ignore
interface IgnoredInterfaceB {
    default boolean getBooleanValue() {
        return false;
    }

    // TODO: add method, and check implemented method in subtype is not ignored
}
