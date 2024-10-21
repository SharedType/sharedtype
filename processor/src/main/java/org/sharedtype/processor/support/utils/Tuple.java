package org.sharedtype.processor.support.utils;

public record Tuple<A, B>(A a, B b) {
    public static <A, B> Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", a, b);
    }
}
