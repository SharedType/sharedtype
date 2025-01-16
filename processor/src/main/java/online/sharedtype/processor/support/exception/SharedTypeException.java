package online.sharedtype.processor.support.exception;

/**
 * Indicate an exception, equivalent to {@link RuntimeException}.
 *
 * @author Cause Chung
 */
public final class SharedTypeException extends RuntimeException {
    private static final long serialVersionUID = -50161145644065579L;

    public SharedTypeException(String message) {
        super(message);
    }

    public SharedTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
