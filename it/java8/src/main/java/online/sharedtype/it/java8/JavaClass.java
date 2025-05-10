package online.sharedtype.it.java8;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import online.sharedtype.SharedType;

@EqualsAndHashCode(callSuper = true)
@Setter
@SharedType
public final class JavaClass extends SuperClassA {
    static final long SOME_LONG_VALUE = 123L;

    @Getter
    private String string;
    @Getter
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
