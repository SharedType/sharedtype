package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;

/**
 * Represents a generic type variable.
 * <br>
 * A type variable refers to a generic type parameter, it has a notation like "T" or bound information like "T extends Number".
 * A type argument is the actual type of the type variable. E.g. {@code "Integer" in "List<Integer>"}.
 *
 * @see ConcreteTypeInfo#typeArgs()
 * @author Cause Chung
 */
@EqualsAndHashCode
@Builder
public final class TypeVariableInfo implements TypeInfo {
    private static final long serialVersionUID = 7632941203572660271L;
    private final String name;
    // TODO: support generic bounds

    public String name() {
        return name;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
