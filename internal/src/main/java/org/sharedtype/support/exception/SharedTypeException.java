package org.sharedtype.support.exception;

/**
 * Indicate an exception, equivalent to {@link RuntimeException}.
 *
 * @author Cause Chung
 */
public final class SharedTypeException extends RuntimeException {
    public SharedTypeException(String message) {
        super(message);
    }

    public SharedTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
