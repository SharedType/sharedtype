package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.FieldComponentInfo;

import java.util.regex.Pattern;

final class ConversionUtils {
    private final static Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z]+)");
    private ConversionUtils() {}

    static String literalValue(Object value) {
        if (value instanceof CharSequence || value instanceof Character) {
            return String.format("\"%s\"", value); // TODO: options single or double quotes?
        } else {
            return String.valueOf(value);
        }
    }

    static String toSnakeCase(String camelCase) {
        return CAMEL_CASE_PATTERN.matcher(camelCase).replaceAll("$1_$2").toLowerCase();
    }

    static boolean isOptionalField(FieldComponentInfo field) {
        if (field.optional()) {
            return true;
        }
        if (field.type() instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo type = (ConcreteTypeInfo) field.type();
            return type.typeDef() != null && type.typeDef().isCyclicReferenced();
        }
        return false;
    }
}
