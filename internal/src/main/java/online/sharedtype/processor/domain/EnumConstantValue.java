package online.sharedtype.processor.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class EnumConstantValue implements ValueHolder {
    private static final long serialVersionUID = -6711930218877737970L;
    @Getter
    private final String enumConstantName;
    @Getter
    private final TypeInfo valueType;
    private final Object value;

    @Override
    public Object value() {
        if (value instanceof ValueHolder) {
            return ((ValueHolder) value).value();
        }
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", enumConstantName, value);
    }
}
