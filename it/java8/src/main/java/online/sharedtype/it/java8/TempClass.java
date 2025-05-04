package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.math.BigDecimal;

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
public class TempClass extends IgnoredSuperClassB {
    //    static final BigDecimal VALUE = new BigDecimal("1.1");
    static final EnumSize REFERENCED_ENUM_VALUE3 = EnumSize.SMALL;
}
