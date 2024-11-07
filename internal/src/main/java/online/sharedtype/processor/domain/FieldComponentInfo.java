package online.sharedtype.processor.domain;

import lombok.Builder;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * Represents a field or accessor.
 *
 * @author Cause Chung
 */
@Builder
public final class FieldComponentInfo implements ComponentInfo {
    private static final long serialVersionUID = -155863067131290289L;
    private final String name;
    private final Set<Modifier> modifiers;
    private final boolean optional;
    private final TypeInfo type;

    public String name() {
        return name;
    }
    public boolean optional() {
        return optional;
    }

    public TypeInfo type() {
        return type;
    }

    @Override
    public boolean resolved() {
        return type.resolved();
    }

    @Override
    public String toString() {
        return String.format("%s %s%s", type, name, optional ? "?" : "");
    }
}
