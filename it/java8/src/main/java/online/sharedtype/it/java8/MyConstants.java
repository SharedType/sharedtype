package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType
final class MyConstants {
    private static final float FLOAT_VALUE = 1.888f;
    private static final Long LONG_VALUE = 999L;
}

@SharedType
enum MyEnumConstants {
    ;
    private static final int INT_VALUE = 1;
    private static final String STR_VALUE = "abc";
}
