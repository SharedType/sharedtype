package online.sharedtype.processor.context;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@UtilityClass
final class EnumParsingUtils {
    /** Match by provided function */
    static <T extends Enum<T>> Set<T> parseEnumSet(Collection<String> values, Class<T> type, Function<String, T> enumValueOf) {
        Set<T> set = EnumSet.noneOf(type);
        for (String trimmed : values) {
            set.add(enumValueOf.apply(trimmed));
        }
        return set;
    }

    /** Match uppercase of values */
    static <T extends Enum<T>> Set<T> parseEnumSet(Collection<String> values, Class<T> type) {
        Set<T> set = EnumSet.noneOf(type);
        for (String trimmed : values) {
            set.add(Enum.valueOf(type, trimmed.toUpperCase()));
        }
        return set;
    }
}
