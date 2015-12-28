package ru.fizteh.fivt.students.JenkaEff.CollectionQuery;

import java.util.function.Function;
import java.util.function.Predicate;

public class Conditions<T> {
    public static <T> Predicate<T> rlike(Function<T, String> expression, String regexp) {
        return e -> expression.apply(e).matches(regexp);
    }
}