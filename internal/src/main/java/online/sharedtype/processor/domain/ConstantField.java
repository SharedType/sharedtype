package online.sharedtype.processor.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;

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
    private final ValueHolder value;

    public String name() {
        return name;
    }

    public TypeInfo type() {
        return type;
    }

    public ValueHolder value() {
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
