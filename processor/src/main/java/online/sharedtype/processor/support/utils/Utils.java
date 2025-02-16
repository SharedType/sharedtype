package online.sharedtype.processor.support.utils;

import lombok.experimental.UtilityClass;
import online.sharedtype.SharedType;

/**
 * @author Cause Chung
 */
@UtilityClass
public final class Utils {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String substringAndUncapitalize(String str, int beginIndex) {
        return Character.toLowerCase(str.charAt(beginIndex)) + str.substring(beginIndex + 1); // TODO: see if can optimize
    }

    public static String[] emptyStringArray() {
        return EMPTY_STRING_ARRAY;
    }
}
