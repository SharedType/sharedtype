package online.sharedtype.it.java8;

import online.sharedtype.SharedType;
import online.sharedtype.it.java8.other.OtherConstants;

import java.math.BigDecimal;
import java.math.BigInteger;

import static online.sharedtype.it.java8.other.OtherConstants.Inner1.STATIC_IMPORTED_VALUE;

@SharedType(constantNamespaced = SharedType.OptionalBool.FALSE, includes = SharedType.ComponentType.CONSTANTS)
final class MyConstants extends IgnoredSuperClassB {
    private static final float FLOAT_VALUE = 1.888f;
    static final long LONG_VALUE = 999L;
    @SharedType.Ignore
    static final long IGNORED_LOCAL_VALUE = 555L;
    static final long REFERENCED_LOCAL_VALUE = IGNORED_LOCAL_VALUE;
    static final long SELF_REFERENCED_LOCAL_VALUE = MyConstants.IGNORED_LOCAL_VALUE;
    static final long REFERENCED_IMPORTED_VALUE = OtherConstants.LONG_VALUE;
    static final long REFERENCED_NESTED_VALUE = OtherConstants.Inner1.Inner2.INNER_LONG_VALUE;
    static final long REFERENCED_STATIC_IMPORTED_VALUE = STATIC_IMPORTED_VALUE;
    static final long DOUBLE_REFERENCED_VALUE = SELF_REFERENCED_LOCAL_VALUE;
    static final long REFERENCED_PACKAGE_PRIVATE_VALUE = JavaClass.SOME_LONG_VALUE;
    static final long REFERENCED_SUPER_VALUE = LONG_VALUE_IN_SUPER;
    static final long SELECTED_SUPER_VALUE = IgnoredSuperClassB.LONG_VALUE_IN_SUPER;
    static final EnumGalaxy REFERENCED_ENUM_VALUE = EnumGalaxy.MilkyWay;
    static final EnumTShirt REFERENCED_ENUM_VALUE2 = EnumTShirt.S;
    static final EnumSize REFERENCED_ENUM_VALUE3 = EnumSize.SMALL;

    static final long REFERENCED_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK;
    static final long SELF_REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_IMPORTED_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_NESTED_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_STATIC_IMPORTED_VALUE_IN_STATIC_BLOCK;
    static final long DOUBLE_REFERENCED_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_PACKAGE_PRIVATE_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_SUPER_VALUE_IN_STATIC_BLOCK;
    static final long SELECTED_SUPER_VALUE_IN_STATIC_BLOCK;
    static final long REFERENCED_STATIC_VALUE_IN_STATIC_BLOCK;
    static final EnumGalaxy REFERENCED_ENUM_VALUE_IN_STATIC_BLOCK;
    static final EnumTShirt REFERENCED_ENUM_VALUE2_IN_STATIC_BLOCK;
    static final EnumSize REFERENCED_ENUM_VALUE3_IN_STATIC_BLOCK;
    static {
        REFERENCED_VALUE_IN_STATIC_BLOCK = 112L;
        REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK = IGNORED_LOCAL_VALUE;
        SELF_REFERENCED_LOCAL_VALUE_IN_STATIC_BLOCK = MyConstants.IGNORED_LOCAL_VALUE;
        REFERENCED_IMPORTED_VALUE_IN_STATIC_BLOCK = OtherConstants.LONG_VALUE;
        REFERENCED_NESTED_VALUE_IN_STATIC_BLOCK = OtherConstants.Inner1.Inner2.INNER_LONG_VALUE;
        REFERENCED_STATIC_IMPORTED_VALUE_IN_STATIC_BLOCK = STATIC_IMPORTED_VALUE;
        DOUBLE_REFERENCED_VALUE_IN_STATIC_BLOCK = SELF_REFERENCED_LOCAL_VALUE;
        REFERENCED_PACKAGE_PRIVATE_VALUE_IN_STATIC_BLOCK = JavaClass.SOME_LONG_VALUE;
        REFERENCED_SUPER_VALUE_IN_STATIC_BLOCK = LONG_VALUE_IN_SUPER;
        SELECTED_SUPER_VALUE_IN_STATIC_BLOCK = IgnoredSuperClassB.LONG_VALUE_IN_SUPER;
        REFERENCED_STATIC_VALUE_IN_STATIC_BLOCK = OtherConstants.Inner1.INNER_LONG_VALUE_IN_STATIC_BLOCK;
        REFERENCED_ENUM_VALUE_IN_STATIC_BLOCK = EnumGalaxy.MilkyWay;
        REFERENCED_ENUM_VALUE2_IN_STATIC_BLOCK = EnumTShirt.S;
        REFERENCED_ENUM_VALUE3_IN_STATIC_BLOCK = EnumSize.SMALL;
    }
    static final BigDecimal MATH_VALUE = new BigDecimal("1.1");
//    static final BigInteger MATH_VALUE_QUALIFIED_NAME = new java.math.BigInteger("88885555");

    @SharedType(constantNamespaced = SharedType.OptionalBool.FALSE, includes = SharedType.ComponentType.CONSTANTS)
    static final class InnerConstantClass {
        static final long INNER_REFERENCED_SUPER_VALUE_IN_STATIC;
        static {
            INNER_REFERENCED_SUPER_VALUE_IN_STATIC = LONG_VALUE_IN_SUPER;
        }
    }
}

@SharedType(includes = SharedType.ComponentType.CONSTANTS)
enum MyEnumConstants {
    ;
    private static final int INT_VALUE = 1;
    private static final String STR_VALUE = "abc";
}
