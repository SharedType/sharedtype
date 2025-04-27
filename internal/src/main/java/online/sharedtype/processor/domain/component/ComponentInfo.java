package online.sharedtype.processor.domain.component;

import online.sharedtype.processor.domain.def.TypeDef;

import java.io.Serializable;

/**
 * Represents internal components in a {@link TypeDef}.
 *
 * @author Cause Chung
 */
public interface ComponentInfo extends Serializable {
    boolean resolved();
}
