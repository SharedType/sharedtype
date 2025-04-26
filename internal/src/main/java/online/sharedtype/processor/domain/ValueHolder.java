package online.sharedtype.processor.domain;

import java.io.Serializable;

public interface ValueHolder extends Serializable {
    Object value();
    default String literalValue() {
        Object value = value();
        if (value instanceof CharSequence || value instanceof Character) {
            return String.format("\"%s\"", value); // TODO: options single or double quotes?
        } else {
            return String.valueOf(value);
        }
    }

    static ValueHolder of(Object value) {
        if (value instanceof ValueHolder) {
            return (ValueHolder) value;
        } else {
            return new LiteralValue(value);
        }
    }

    static EnumConstantValue ofEnum(String enumConstantName, TypeInfo valueType, Object value) {
        return new EnumConstantValue(enumConstantName, valueType, of(value));
    }

    static EnumConstantValue ofEnum(String enumConstantName) {
        return new EnumConstantValue(enumConstantName, Constants.STRING_TYPE_INFO, enumConstantName);
    }

    LiteralValue NULL = new LiteralValue(null);
}
