package org.sharedtype.processor.support.exception;

public final class SharedTypeException extends RuntimeException {
    public SharedTypeException(String message) {
        super(message);
    }

    public SharedTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
