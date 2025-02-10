package online.sharedtype.processor.writer.converter;

import java.util.regex.Pattern;

final class LiteralUtils {
    private final static Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z]+)");
    private LiteralUtils() {}

    static String literalValue(Object value) {
        if (shouldQuote(value)) {
            return String.format("\"%s\"", value); // TODO: options single or double quotes?
        } else {
            return String.valueOf(value);
        }
    }

    private static boolean shouldQuote(Object value) {
        return value instanceof CharSequence || value instanceof Character;
    }

    static String toSnakeCase(String camelCase) {
        return CAMEL_CASE_PATTERN.matcher(camelCase).replaceAll("$1_$2").toLowerCase();
    }
}
