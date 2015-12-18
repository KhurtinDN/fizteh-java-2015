package library.api;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * "where" statement conditions.
 */
public class Conditions {

    public static <T> Predicate<T> like(Function<T, String> source, String mask) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(mask);
        return t -> {
            Objects.requireNonNull(t);
            String actual = source.apply(t);
            return actual.matches(mask.replace("%", ".*"));
        };
    }

    public static <T> Predicate<T> not(Predicate<T> original) {
        return original.negate();
    }
}
