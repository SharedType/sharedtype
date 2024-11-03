package org.sharedtype.domain;

import java.io.Serializable;

/**
 * Represents internal components in a {@link TypeDef}.
 *
 * @author Cause Chung
 */
public interface ComponentInfo extends Serializable {
    boolean resolved();
}
