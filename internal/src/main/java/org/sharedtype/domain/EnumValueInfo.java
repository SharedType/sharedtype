package org.sharedtype.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.sharedtype.annotation.SharedType;

/**
 * Represents an enum value, which is the value in the target code that corresponds to an enum constant.
 * <br>
 * By default, enum value is String value of the enum constant. It can be configured with {@link SharedType.EnumValue}.
 *
 * @see EnumDef
 * @see SharedType.EnumValue
 * @author Cause Chung
 */
@EqualsAndHashCode
@RequiredArgsConstructor
public final class EnumValueInfo implements ComponentInfo {
    private final TypeInfo type;
    private final Object value;

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
        return value.toString();
    }
}
