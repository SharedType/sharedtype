package online.sharedtype.processor.domain;

import java.io.Serializable;

/**
 * Represents internal components in a {@link TypeDef}.
 *
 * @author Cause Chung
 */
public interface ComponentInfo extends Serializable {
    boolean resolved();
}
