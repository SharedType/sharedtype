package online.sharedtype.processor.domain.component;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.value.EnumConstantValue;

/**
 * Represents an enum value, which is the value in the target code that corresponds to an enum constant.
 * <br>
 * By default, enum value is String value of the enum constant. It can be configured with {@link SharedType.EnumValue}.
 *
 * @see EnumDef
 * @see SharedType.EnumValue
 * @author Cause Chung
 */
@EqualsAndHashCode(of = {"name", "value"}, callSuper = false)
@SuperBuilder
public final class EnumValueInfo extends AbstractComponentInfo {
    private static final long serialVersionUID = 1117324458104635595L;
    private final String name;
    private final EnumConstantValue value;

    public String name() {
        return name;
    }

    public EnumConstantValue value() {
        return value;
    }

    @Override
    public boolean resolved() {
        return value.getValueType().resolved();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
