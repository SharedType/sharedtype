package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType
public class TempClass {
    public static final String CONST_VALUE = "abc";
}

@SharedType
enum TempEnum {
    VALUE1;

    public static final String CONST_VALUE2 = "ddd";
}
