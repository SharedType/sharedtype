package online.sharedtype.processor.writer.converter;

final class LiteralUtils {
    private LiteralUtils() {}

    static boolean shouldQuote(Object value) {
        return value instanceof CharSequence || value instanceof Character;
    }

    static String toSnakeCase(String camelCase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
