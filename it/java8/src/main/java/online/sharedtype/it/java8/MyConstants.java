package online.sharedtype.it.java8;

import online.sharedtype.SharedType;
import online.sharedtype.it.java8.other.OtherConstants;

import static online.sharedtype.it.java8.other.OtherConstants.Inner1.STATIC_IMPORTED_VALUE;

@SharedType(constantNamespaced = SharedType.OptionalBool.FALSE, includes = SharedType.ComponentType.CONSTANTS)
final class MyConstants extends IgnoredSuperClassB {
    private static final float FLOAT_VALUE = 1.888f;
    static final long LONG_VALUE = 999L;
    @SharedType.Ignore
    static long IGNORED_LOCAL_VALUE = 555L;
    static final long REFERENCED_LOCAL_VALUE = IGNORED_LOCAL_VALUE;
    static final long SELF_REFERENCED_LOCAL_VALUE = MyConstants.IGNORED_LOCAL_VALUE;
    static final long REFERENCED_IMPORTED_VALUE = OtherConstants.LONG_VALUE;
    static final long REFERENCED_NESTED_VALUE = OtherConstants.Inner1.Inner2.INNER_LONG_VALUE;
    static final long REFERENCED_STATIC_IMPORTED_VALUE = STATIC_IMPORTED_VALUE;
    static final long DOUBLE_REFERENCED_VALUE = SELF_REFERENCED_LOCAL_VALUE;
    static final long REFERENCED_PACKAGE_PRIVATE_VALUE = JavaClass.SOME_LONG_VALUE;
    static final long REFERENCED_SUPER_VALUE = LONG_VALUE_IN_SUPER;
    static final long SELECTED_SUPER_VALUE = IgnoredSuperClassB.LONG_VALUE_IN_SUPER;

    static final long REFERENCED_VALUE_IN_STATIC_BLOCK;
    static {
        REFERENCED_VALUE_IN_STATIC_BLOCK = 112L;
    }
}

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
enum MyEnumConstants {
    ;
    private static final int INT_VALUE = 1;
    private static final String STR_VALUE = "abc";
}
