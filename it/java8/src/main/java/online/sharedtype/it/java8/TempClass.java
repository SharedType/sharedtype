package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import static online.sharedtype.it.java8.other.OtherConstants.Inner1.Inner2.INNER_LONG_VALUE;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass {
    static final long REFERENCED_VALUE = INNER_LONG_VALUE;
}
