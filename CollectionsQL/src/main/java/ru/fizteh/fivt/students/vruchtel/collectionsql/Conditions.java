package ru.fizteh.fivt.students.vruchtel.collectionsql;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Conditions<T> {
    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        return element -> expression.apply(element).matches(regexp);
    }

    public static <T> Predicate<T> like(Function<T, String> expression, String pattern) {
        throw new UnsupportedOperationException();
    }
}