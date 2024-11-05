package org.sharedtype.support.utils;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author Cause Chung
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Tuple<A, B> {
    private final A a;
    private final B b;

    public static <A, B> Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", a, b);
    }
}
