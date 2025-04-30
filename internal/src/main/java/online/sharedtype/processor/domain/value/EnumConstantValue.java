package online.sharedtype.processor.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.processor.domain.type.TypeInfo;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class EnumConstantValue extends LiteralValue {
    private static final long serialVersionUID = -6711930218877737970L;
    private final String enumConstantName;
    EnumConstantValue(String enumConstantName, TypeInfo valueType, Object value) {
        super(valueType, value);
        this.enumConstantName = enumConstantName;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", enumConstantName, getValue());
    }
}
