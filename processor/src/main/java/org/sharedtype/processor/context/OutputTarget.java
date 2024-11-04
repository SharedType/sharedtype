package org.sharedtype.processor.context;

/**
 * @author Cause Chung
 */
public enum OutputTarget {
    /** Print metadata to console. */
    CONSOLE,
    /** Write metadata to Java serialized file. Used for integration test. */
    JAVA_SERIALIZED,
    TYPESCRIPT,
    GO,
    RUST,
}
