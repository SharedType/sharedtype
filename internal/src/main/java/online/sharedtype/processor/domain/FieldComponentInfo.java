package online.sharedtype.processor.domain;

import lombok.Builder;
import lombok.Setter;
import online.sharedtype.processor.domain.type.TypeInfo;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * Represents a field or accessor.
 *
 * @author Cause Chung
 */
@Builder(toBuilder = true)
public final class FieldComponentInfo implements ComponentInfo {
    private static final long serialVersionUID = -155863067131290289L;
    private final String name;
    private final Set<Modifier> modifiers;
    @Setter
    private TypeInfo type;
    @Setter
    private boolean optional;

    public String name() {
        return name;
    }
    public boolean optional() {
        return optional;
    }

    public TypeInfo type() {
        return type;
    }

    public Set<Modifier> modifiers() {
        return modifiers;
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
