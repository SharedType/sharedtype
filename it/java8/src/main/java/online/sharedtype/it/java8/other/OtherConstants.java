package online.sharedtype.it.java8.other;

public final class OtherConstants {
    public static final long LONG_VALUE = 666L;

    public static final class Inner1 {
        public static final long STATIC_IMPORTED_VALUE = 999L;
        public static final long INNER_LONG_VALUE_IN_STATIC_BLOCK;
        static {
            INNER_LONG_VALUE_IN_STATIC_BLOCK = 787L;
        }

        public static final class Inner2 {
            public static final long INNER_LONG_VALUE = 777L;
        }
    }
}
