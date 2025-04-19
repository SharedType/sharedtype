package online.sharedtype.it.java8;

import online.sharedtype.SharedType;
import online.sharedtype.it.java8.other.OtherConstants;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass {
    static final long REFERENCED_VALUE = OtherConstants.Inner1.Inner2.INNER_LONG_VALUE;
}
