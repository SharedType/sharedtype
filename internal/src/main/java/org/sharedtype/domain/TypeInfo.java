package org.sharedtype.domain;

import java.io.Serializable;

/**
 * Type information.
 *
 * @see TypeDef
 * @author Cause Chung
 */
public interface TypeInfo extends Serializable {
    /**
     * <p>Check if this type and its dependency types are resolved.</p>
     * <p>
     *     When parsing a type's structure, a dependency type is firstly captured as a {@link TypeInfo}.
     *     At this stage, because we don't know its output structure or if it needs output at all, we mark it as unresolved.
     *     Also due to possible cyclic dependencies, the resolution stage needs to be performed after initial parsing state.
     *     During resolution, once a type is parsed, it's marked as resolved.
     *     Note that a type is marked as resolved when created, if it can be determined at that time.
     * </p>
     * <p>Object contains resolved flag as a mutable state</p>
     *
     * @return true is this type and its dependency types are resolved.
     */
    boolean resolved();
}
