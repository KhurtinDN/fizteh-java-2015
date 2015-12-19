package mini.orm.core.utils;

public final class NameUtils {

    /**
     * convert CamelCase format string to underscore format using regexp
     */
    public static String convertFromCamelCaseToUnderscore(String ccStr) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return ccStr.replaceAll(regex, replacement).toLowerCase();
    }
}