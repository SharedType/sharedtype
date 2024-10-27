package org.sharedtype.processor.support;

import org.sharedtype.processor.support.exception.SharedTypeInternalError;

import java.util.Collection;

// TODO: remove varargs to improve performance
public final class Preconditions {
    private Preconditions() {
    }


    public static void checkArgument(boolean condition, String message, Object... objects) {
        if (!condition) {
            throw new SharedTypeInternalError(String.format(message, objects));
        }
    }

    public static <T> T requireNonNull(T o, String message, Object... objects) {
        if (o == null) {
            throw new SharedTypeInternalError(String.format(message, objects));
        }
        return o;
    }

    public static <T extends Collection<?>> T requireNonEmpty(T c, String message, Object... objects) {
        if (c.isEmpty()) {
            throw new SharedTypeInternalError(String.format(message, objects));
        }
        return c;
    }
}
