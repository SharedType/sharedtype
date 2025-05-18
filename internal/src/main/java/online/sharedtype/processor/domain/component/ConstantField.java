package online.sharedtype.processor.domain.component;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import online.sharedtype.processor.domain.type.TypeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;

/**
 * Represents a constant literal.
 * Only literals with values resolvable at compile time are supported.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode(of = {"value"}, callSuper = true)
@SuperBuilder
public final class ConstantField extends AbstractComponentInfo {
    private static final long serialVersionUID = -155863067131290289L;
    private final ValueHolder value;

    public ValueHolder value() {
        return value;
    }

    @Override
    public boolean resolved() {
        return value.getValueType().resolved();
    }

    @Override
    public String toString() {
        return String.format("%s=%s", name(), value);
    }
}
