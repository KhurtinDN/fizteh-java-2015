package ru.fizteh.fivt.students.JenkaEff.CollectionQuery;

import java.util.Comparator;
import java.util.function.Function;

public class OrderByConditions {
    public static <T, R extends Comparable<R>> Comparator<T> asc(Function<T, R> expression) {
        return (e1, e2) -> expression.apply(e1).compareTo(expression.apply(e2));
    }

    public static <T, R extends Comparable<R>> Comparator<T> desc(Function<T, R> expression) {
        return (e1, e2) -> expression.apply(e2).compareTo(expression.apply(e1));
    }
}