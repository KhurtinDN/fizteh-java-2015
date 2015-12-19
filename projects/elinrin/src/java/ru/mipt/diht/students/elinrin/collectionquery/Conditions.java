package ru.mipt.diht.students.elinrin.collectionquery;

import java.util.function.Function;
import java.util.function.Predicate;

public class Conditions<T> {
    public static <T> Predicate<T> rlike(final Function<T, String> expression, final String regexp) {
        return element -> expression.apply(element).matches(regexp);
    }

    public static <T> Predicate<T> like(final Function<T, String> expression, final String pattern) {
        throw new UnsupportedOperationException();
    }
}
