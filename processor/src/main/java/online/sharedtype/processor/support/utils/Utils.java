package online.sharedtype.processor.support.utils;

import lombok.experimental.UtilityClass;
import online.sharedtype.SharedType;
import online.sharedtype.processor.support.exception.SharedTypeException;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Cause Chung
 */
@UtilityClass
public final class Utils {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String substringAndUncapitalize(String str, int beginIndex) {
        try{
            return Character.toLowerCase(str.charAt(beginIndex)) + str.substring(beginIndex + 1); // TODO: see if can optimize
        } catch (IndexOutOfBoundsException e) {
            throw new SharedTypeInternalError(String.format("Failed to substringAndUncapitalize string: '%s'", str) ,e);
        }
    }

    public static String[] emptyStringArray() {
        return EMPTY_STRING_ARRAY;
    }

    public static String notEmptyOrDefault(String value, String defaultValue, Supplier<String> message) {
        String res = value != null && !value.isEmpty() ? value : defaultValue;
        if (res == null || res.isEmpty()) {
            throw new SharedTypeException("Either value or defaultValue must not be empty. " + message.get());
        }
        return res;
    }

    public static List<VariableElement> allInstanceFields(TypeElement typeElement) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        List<VariableElement> fields = new ArrayList<>(enclosedElements.size());
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.FIELD && !enclosedElement.getModifiers().contains(Modifier.STATIC)) {
                fields.add((VariableElement) enclosedElement);
            }
        }
        return fields;
    }
}
