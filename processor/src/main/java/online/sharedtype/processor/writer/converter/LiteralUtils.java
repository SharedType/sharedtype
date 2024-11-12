package online.sharedtype.processor.writer.converter;

final class LiteralUtils {
    private LiteralUtils() {}

    static boolean shouldQuote(Object value) {
        return value instanceof CharSequence || value instanceof Character;
    }
}
