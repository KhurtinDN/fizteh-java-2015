package ru.mipt.diht.students.simon23rus.CQL.data;

import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.Avg;
import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.Count;
import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.Max;
import ru.mipt.diht.students.simon23rus.CQL.implOfAggregators.Min;

import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T, R extends Comparable> Function<T, R> max(Function<T, R> expression) {
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
     * @param <T>
     * @return
     */
    public static <T> Function<T, Long> count(Function<T, ?> expression) {
        return new Count<>(expression);
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <T>
     * @return
     */
    public static <T> Function<T, Double> avg(Function<T, Double> expression) {
        return new Avg<>(expression);
    }

}
