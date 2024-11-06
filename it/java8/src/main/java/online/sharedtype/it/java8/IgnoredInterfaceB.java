package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType.Ignore
interface IgnoredInterfaceB {
    default boolean getBooleanValue() {
        return false;
    }

    int getNotIgnoredImplementedMethod();
}
