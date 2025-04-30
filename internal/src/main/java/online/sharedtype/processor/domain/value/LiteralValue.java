package online.sharedtype.processor.domain.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.domain.type.TypeInfo;

import java.util.Objects;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LiteralValue implements ValueHolder {
    private static final long serialVersionUID = -7324230239169028973L;
    private final TypeInfo valueType;
    private final Object value;

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
