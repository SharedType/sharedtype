package org.sharedtype.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Type definition. Represents type structure used for target output.
 * This is the unit for target code generation.
 * On the other hand, {@link TypeInfo} represents a type without structural information, which is referenced in {@link TypeDef}.
 *
 * @see TypeInfo
 * @author Cause Chung
 */
public interface TypeDef extends Serializable {
    String qualifiedName();

    String simpleName();

    List<? extends ComponentInfo> components();

    /**
     * @return true if all required types are resolved.
     */
    boolean resolved();
}
