package online.sharedtype.processor.support.exception;

/**
 * Indicate an exception, equivalent to {@link RuntimeException}.
 * Note: compilation error should be printed via {@link online.sharedtype.processor.context.Context#error}.
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
