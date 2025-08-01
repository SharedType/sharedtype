package online.sharedtype.exec.common;

/**
 * Adapter for logging.
 * @author Cause Chung
 */
public interface Logger {
    void info(String message);
    void warn(String message);
    void error(String message);
}
