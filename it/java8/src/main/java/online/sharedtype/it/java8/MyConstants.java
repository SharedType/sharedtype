package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType(constantNamespaced = SharedType.OptionalBool.FALSE, includes = SharedType.ComponentType.CONSTANTS)
final class MyConstants {
    private static final float FLOAT_VALUE = 1.888f;
    private static final Long LONG_VALUE = 999L;
//    private static final Long REFERENCED_VALUE = MyConstants.LONG_VALUE;
}

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
enum MyEnumConstants {
    ;
    private static final int INT_VALUE = 1;
    private static final String STR_VALUE = "abc";
}
