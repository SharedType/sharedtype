package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass {
    static final long REFERENCED_VALUE = MyConstants.LONG_VALUE;
}
