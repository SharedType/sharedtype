package online.sharedtype.processor.domain.type;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;
import online.sharedtype.processor.domain.MappableType;

import online.sharedtype.processor.support.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a date/time type, which is simply be emitted as a string or any custom type literal.
 * It has no counterpart typeDef, explicitly annotating {@code @SharedType} on a date/time type is ignored with a warning.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode(of = "qualifiedName", callSuper = false)
@RequiredArgsConstructor
public final class DateTimeInfo extends ReferableTypeInfo implements MappableType {
    private static final long serialVersionUID = 5428192893749821359L;

    private final String qualifiedName;
    /** Defined type mapping, see {@link SharedType} for details */
    private final Map<SharedType.TargetType, String> mappedNames = new EnumMap<>(SharedType.TargetType.class);

    public String qualifiedName() {
        return qualifiedName;
    }

    @Nullable
    @Override
    public String mappedName(@Nullable SharedType.TargetType targetType) {
        return targetType == null ? null : mappedNames.get(targetType);
    }

    @Override
    public void addMappedName(SharedType.TargetType targetType, String mappedName) {
        mappedNames.put(targetType, mappedName);
    }

    @Override
    public String toString() {
        return String.format("DateTime(%s)", qualifiedName);
    }
}
