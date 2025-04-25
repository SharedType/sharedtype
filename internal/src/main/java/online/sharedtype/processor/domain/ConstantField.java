package online.sharedtype.processor.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents a constant literal.
 * Only literals with values resolvable at compile time are supported.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class ConstantField implements ComponentInfo {
    private static final long serialVersionUID = -155863067131290289L;
    private final String name;
    private final TypeInfo type;
    private final Object value;

    public String name() {
        return name;
    }

    public TypeInfo type() {
        return type;
    }

    public Object value() {
        return value;
    }

    @Override
    public boolean resolved() {
        return type.resolved();
    }

    @Override
    public String toString() {
        return String.format("%s=%s", name, value);
    }
}
