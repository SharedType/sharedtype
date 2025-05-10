package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.component.FieldComponentInfo;

import java.util.Set;
import java.util.regex.Pattern;

final class ConversionUtils {
    private final static Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z]+)");
    private ConversionUtils() {}

    static String toSnakeCase(String camelCase) {
        return CAMEL_CASE_PATTERN.matcher(camelCase).replaceAll("$1_$2").toLowerCase();
    }

    static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    static boolean isOptionalField(FieldComponentInfo field) {
        if (field.optional()) {
            return true;
        }
        return isOfCyclicReferencedType(field);
    }

    static boolean isOfCyclicReferencedType(FieldComponentInfo field) {
        if (field.type() instanceof ConcreteTypeInfo) {
            ConcreteTypeInfo type = (ConcreteTypeInfo) field.type();
            return type.typeDef() != null && type.typeDef().isCyclicReferenced();
        }
        return false;
    }

    static String buildRustMacroTraitsExpr(Set<String> macroTraits) {
        if (macroTraits.isEmpty()) {
            return null;
        }
        return String.format("#[derive(%s)]", String.join(", ", macroTraits));
    }
}
