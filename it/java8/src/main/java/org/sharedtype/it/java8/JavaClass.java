package org.sharedtype.it.java8;

import org.sharedtype.annotation.SharedType;

@SharedType
class JavaClass extends SuperClassA {
    private String string;
    private EnumSize size;
//    private IgnoredInterfaceB b; // compilation failure
    private @SharedType.Ignore IgnoredInterfaceB ignoredB;

    @Override
    public int getNotIgnoredImplementedMethod() {
        return 1;
    }

    @SharedType
    static class InnerClass {
        private int value;
    }
}
