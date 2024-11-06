package online.sharedtype.support.utils;

/**
 * @author Cause Chung
 */
public final class Utils {

    private Utils() {
    }

    public static String substringAndUncapitalize(String str, int beginIndex) {
        return Character.toLowerCase(str.charAt(beginIndex)) + str.substring(beginIndex + 1); // TODO: see if can optimize
    }

}
