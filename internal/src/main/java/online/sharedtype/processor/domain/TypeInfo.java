package online.sharedtype.processor.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Type information. Similar concept as {@link javax.lang.model.type.TypeMirror}.
 *
 * @author Cause Chung
 * @see TypeDef
 */
public interface TypeInfo extends Serializable {
    /**
     * <p>Check if this type and its dependency types are resolved.</p>
     * <p>
     * When parsing a type's structure, a dependency type is firstly captured as a {@link TypeInfo}.
     * At this stage, because we don't know its output structure or if it needs to be output at all, we mark it as unresolved.
     * Also, due to possible cyclic dependencies, the resolution stage needs to be performed after initial parsing state.
     * During resolution, once a type is parsed, it's marked as resolved.
     * Note that a type is marked as resolved when created, if it can be determined at that time.
     * </p>
     * <p>Object contains resolved flag as a mutable state</p>
     *
     * @return true is this type and its dependency types are resolved.
     */
    default boolean resolved() {
        return true;
    }

    /**
     * Replace type variables with type arguments.
     * @param mappings key is a type variable e.g. T
     *                 value is a type argument, a concrete type e.g. Integer, or a generic type with concrete type parameter, e.g. {@code Tuple<String, String>}
     * @return a newly created type info if updated.
     */
    default TypeInfo reify(Map<TypeVariableInfo, TypeInfo> mappings) {
        return this;
    }
}
