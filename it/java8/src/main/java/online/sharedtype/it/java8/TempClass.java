package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass extends IgnoredSuperClassB{
    @SharedType.Ignore
    static long IGNORED_LOCAL_VALUE = 555L;

    static final long REFERENCED_VALUE;
    static {
        REFERENCED_VALUE = IGNORED_LOCAL_VALUE;
    }
}
