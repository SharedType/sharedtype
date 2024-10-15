package org.sharedtype.processor.support;

import java.util.Collection;

// TODO: remove varargs to improve performance
public final class Preconditions {
    private Preconditions() {
    }


    public static void checkArgument(boolean condition, String message, Object... objects) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(message, objects));
        }
    }

    public static <T extends Collection<?>> T requireNonEmpty(T c, String message, Object... objects) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, objects));
        }
        return c;
    }
}
