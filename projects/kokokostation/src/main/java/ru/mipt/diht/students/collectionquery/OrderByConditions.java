package ru.mipt.diht.students.collectionquery;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * OrderBy sort order helper methods.
 */
public class OrderByConditions {

    /**
     * Ascending comparator.
     *
     * @param expression
     * @param <T> - changed
     * @param <R>
     * @return
     */
    public static <T, R extends Comparable<R>> Comparator<List<T>> asc(Function<T, R> expression) {
        return (o1, o2) -> expression.apply(o1.get(0)).compareTo(expression.apply(o2.get(0)));
    }

    public static <T, R extends Comparable<R>> Comparator<List<T>> asc(AggregateFunction<T, R> expression) {
        return (o1, o2) -> expression.apply(o1).compareTo(expression.apply(o2));
    }

    /**
     * Descending comparator.
     *
     * @param expression
     * @param <T> - changed
     * @param <R>
     * @return
     */
    public static <T, R extends Comparable<R>> Comparator<List<T>> desc(Function<T, R> expression) {
        return (o1, o2) -> -asc(expression).compare(o1, o2);
    }

    public static <T, R extends Comparable<R>> Comparator<List<T>> desc(AggregateFunction<T, R> expression) {
        return (o1, o2) -> -asc(expression).compare(o1, o2);
    }

}
