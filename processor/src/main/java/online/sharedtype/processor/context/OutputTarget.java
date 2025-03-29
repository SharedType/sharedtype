package online.sharedtype.processor.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.TargetCodeType;

import javax.annotation.Nullable;

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
    TYPESCRIPT(TargetCodeType.TYPESCRIPT),
    GO(TargetCodeType.GO),
    RUST(TargetCodeType.RUST),
    ;
    @Nullable
    private final TargetCodeType targetCodeType;

    OutputTarget() {
        this(null);
    }
}
