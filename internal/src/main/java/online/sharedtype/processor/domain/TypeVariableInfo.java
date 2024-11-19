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
    private final String contextTypeQualifiedName;
    private final String name;
    private String qualifiedName;
    // TODO: support generic bounds

    public static String concatQualifiedName(String contextTypeQualifiedName, String name) {
        return contextTypeQualifiedName + "@" + name;
    }

    public String contextTypeQualifiedName() {
        return contextTypeQualifiedName;
    }

    public String name() {
        return name;
    }

    public String qualifiedName() {
        if (qualifiedName == null) {
            qualifiedName = concatQualifiedName(contextTypeQualifiedName, name);
        }
        return qualifiedName;
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
