package io.github.cuzfrog.sharedtype.it.java8;

import io.github.cuzfrog.sharedtype.SharedType;

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
