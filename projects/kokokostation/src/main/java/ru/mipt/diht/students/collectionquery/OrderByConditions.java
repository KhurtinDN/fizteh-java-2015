package ru.mipt.diht.students.collectionquery;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * OrderBy sort order helper methods.
 */
public class OrderByConditions {

    /**
     * Ascending comparator.
     *
     * @param expression
     * @param <T>
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
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R extends Comparable<R>> Comparator<List<T>> desc(Function<T, R> expression) {
        return (o1, o2) -> reverseOrder(asc(expression).compare(o1, o2));
    }

    public static <T, R extends Comparable<R>> Comparator<List<T>> desc(AggregateFunction<T, R> expression) {
        return (o1, o2) -> reverseOrder(asc(expression).compare(o1, o2));
    }

    public static int reverseOrder(int order) {
        if (order < 0) {
            return 1;
        } else if (order > 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
