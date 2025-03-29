package online.sharedtype.processor.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import online.sharedtype.SharedType;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a date/time type, which is simply be emitted as a string or any custom type literal.
 * It has no counterpart typeDef, explicitly annotating {@code @SharedType} on a date/time type is ignored with a warning.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode(of = "qualifiedName")
@RequiredArgsConstructor
public final class DateTimeInfo implements TypeInfo, MappableType {
    private static final long serialVersionUID = 5428192893749821359L;

    private final String qualifiedName;
    /** Defined type mapping, see {@link SharedType} for details */
    private final Map<TargetCodeType, String> mappedNames = new EnumMap<>(TargetCodeType.class);

    public String qualifiedName() {
        return qualifiedName;
    }

    @Nullable
    @Override
    public String mappedName(@Nullable TargetCodeType targetCodeType) {
        return targetCodeType == null ? null : mappedNames.get(targetCodeType);
    }

    @Override
    public void addMappedName(TargetCodeType targetCodeType, String mappedName) {
        mappedNames.put(targetCodeType, mappedName);
    }

    @Override
    public String toString() {
        return String.format("DateTime(%s)", qualifiedName);
    }
}
