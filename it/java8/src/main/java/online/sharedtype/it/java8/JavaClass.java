package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType
class JavaClass extends SuperClassA {
    static final long SOME_LONG_VALUE = 123L;

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
