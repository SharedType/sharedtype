package online.sharedtype.processor.domain.component;

import lombok.Setter;
import lombok.experimental.SuperBuilder;
import online.sharedtype.processor.domain.type.TypeInfo;

import javax.lang.model.element.Modifier;
import java.util.Set;

/**
 * Represents a field or accessor.
 *
 * @author Cause Chung
 */
@SuperBuilder(toBuilder = true)
public final class FieldComponentInfo extends AbstractComponentInfo {
    private static final long serialVersionUID = -155863067131290289L;
    @Setter
    private TypeInfo type;
    @Setter
    private boolean optional;

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
        return String.format("%s %s%s", type, name(), optional ? "?" : "");
    }
}
