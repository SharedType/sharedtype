package online.sharedtype.processor.support.utils;

import lombok.experimental.UtilityClass;
import online.sharedtype.SharedType;
import online.sharedtype.processor.support.exception.SharedTypeInternalError;

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
}
