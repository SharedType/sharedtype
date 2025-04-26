package online.sharedtype.processor.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Objects;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class LiteralValue implements ValueHolder {
    private static final long serialVersionUID = -7324230239169028973L;
    private final Object value;

    @Override
    public Object value() {
        Object v = value;
        while (v instanceof ValueHolder) {
            v = ((ValueHolder) v).value();
        }
        return v;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
