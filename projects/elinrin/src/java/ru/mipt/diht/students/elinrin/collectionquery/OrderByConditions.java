package ru.mipt.diht.students.elinrin.collectionquery;

import java.util.Comparator;
import java.util.function.Function;

public final class OrderByConditions {

    public static <T, R extends Comparable<R>> Comparator<T> asc(final Function<T, R> expression) {
        return (o1, o2) -> expression.apply(o1).compareTo(expression.apply(o2));
    }

    public static <T, R extends Comparable<R>> Comparator<T> desc(final Function<T, R> expression) {
        return (o1, o2) -> expression.apply(o2).compareTo(expression.apply(o1));
    }

}
