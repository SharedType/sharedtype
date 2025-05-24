package online.sharedtype.processor.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

import online.sharedtype.processor.support.annotation.Nullable;

/**
 * @author Cause Chung
 */
@Getter
@RequiredArgsConstructor
public enum OutputTarget {
    /** Print metadata to console. */
    CONSOLE,
    /** Write metadata to Java serialized file. Used for integration test. */
    JAVA_SERIALIZED,
    TYPESCRIPT(SharedType.TargetType.TYPESCRIPT),
    GO(SharedType.TargetType.GO),
    RUST(SharedType.TargetType.RUST),
    ;
    @Nullable
    private final SharedType.TargetType targetType;

    OutputTarget() {
        this(null);
    }
}
