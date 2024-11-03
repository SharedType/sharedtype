package org.sharedtype.it.java8;

import org.sharedtype.annotation.SharedType;

@SharedType
class JavaClass {
    private String string;
    private EnumSize size;

    @SharedType
    static class InnerClass {
        private int value;
    }
}
