package online.sharedtype.processor.context;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@UtilityClass
final class EnumParsingUtils {
    static <T extends Enum<T>> Set<T> parseEnumSet(Collection<String> values, Class<T> type, Function<String, T> enumValueOf) {
        Set<T> set = EnumSet.noneOf(type);
        for (String trimmed : values) {
            set.add(enumValueOf.apply(trimmed));
        }
        return set;
    }
}
