package ru.mipt.diht.students.ale3otik.collectionquery;

import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Avg;
import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Count;
import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Max;
import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Min;

import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new Max<>(expression);
    }

    /**
     * Minimum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new Min<>(expression);
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <C>
     * @return
     */
    public static <C> Function<C, Long> count(Function<C, ?> expression) {
        return new Count<>(expression);
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @return
     */
    public static <C> Function<C, Double> avg(Function<C, ? extends Number> expression) {
        return new Avg<>(expression);
    }

}
