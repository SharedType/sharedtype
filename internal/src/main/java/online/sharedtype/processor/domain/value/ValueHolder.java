package online.sharedtype.processor.domain.value;

import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.type.TypeInfo;

import java.io.Serializable;

public interface ValueHolder extends Serializable {
    ConcreteTypeInfo getValueType();
    Object getValue();

    default String literalValue() {
        Object value = getValue();
        if (value instanceof CharSequence || value instanceof Character) {
            return String.format("\"%s\"", value); // TODO: options single or double quotes?
        } else {
            return String.valueOf(value);
        }
    }

    static ValueHolder of(ConcreteTypeInfo valueType, Object value) {
        if (value instanceof ValueHolder) {
            return (ValueHolder) value;
        } else {
            return new LiteralValue(valueType, value);
        }
    }

    static EnumConstantValue ofEnum(String enumConstantName, ConcreteTypeInfo valueType, Object value) {
        ConcreteTypeInfo actualValueType = valueType;
        Object actualValue = value;
        while (actualValue instanceof ValueHolder) {
            ValueHolder valueHolder = (ValueHolder) actualValue;
            actualValueType = valueHolder.getValueType();
            actualValue = valueHolder.getValue();
        }
        return new EnumConstantValue(enumConstantName, actualValueType, actualValue);
    }

    LiteralValue NULL = new LiteralValue(null,null);
}
