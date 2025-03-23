package online.sharedtype.processor.domain;

import lombok.RequiredArgsConstructor;

/**
 * Represents a date/time type, which is simply be emitted as a string or any custom type literal.
 * It has no counterpart typeDef, explicitly annotating {@code @SharedType} on a date/time type is ignored with a warning.
 *
 * @author Cause Chung
 */
@RequiredArgsConstructor
public final class DateTimeInfo implements TypeInfo {
    private static final long serialVersionUID = 5428192893749821359L;

    private final String qualifiedName;

    public String qualifiedName() {
        return qualifiedName;
    }

    @Override
    public String toString() {
        return String.format("DateTime(%s)", qualifiedName);
    }
}
