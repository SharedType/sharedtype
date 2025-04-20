package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass extends IgnoredSuperClassB{
    static final long REFERENCED_VALUE;
    static {
        REFERENCED_VALUE = 112L;
    }
}
