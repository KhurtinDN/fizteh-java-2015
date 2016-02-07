package ru.mipt.diht.students.collectionquery;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Where clause conditions.
 */
public class Conditions<T> {

    /**
     * Matches string result of expression against regexp pattern.
     *
     * @param expression expression result to match
     * @param regexp     pattern to match to
     * @param <T>        source object type
     * @return
     */
    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        return p -> expression.apply(p).matches(regexp);
    }

    /**
     * Matches string result of expression against SQL like pattern.
     *
     * @param expression expression result to match
     * @param pattern    pattern to match to
     * @param <T>        source object type
     * @return
     */
    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
        StringBuilder regexp = new StringBuilder();
        pattern = pattern.toLowerCase();
        boolean inQuotes = false;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (c == '%' || c == '_') {
                String replace = c == '%' ? ".*" : ".";
                regexp.append((inQuotes ? "\\E" : "") + replace);
                if (inQuotes) {
                    inQuotes = false;
                }
            } else if (!inQuotes) {
                regexp.append("\\Q" + c);
                inQuotes = true;
            } else if (i == pattern.length() - 1) {
                regexp.append(c + "\\E");
            } else {
                regexp.append(c);
            }
        }

        return rlike(t -> expression.apply(t).toLowerCase(), regexp.toString());
    }
}
