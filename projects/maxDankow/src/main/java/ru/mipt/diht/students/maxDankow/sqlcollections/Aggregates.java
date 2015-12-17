package ru.mipt.diht.students.maxDankow.sqlcollections;

import ru.mipt.diht.students.maxDankow.sqlcollections.aggregator.Average;
import ru.mipt.diht.students.maxDankow.sqlcollections.aggregator.Count;
import ru.mipt.diht.students.maxDankow.sqlcollections.aggregator.Max;
import ru.mipt.diht.students.maxDankow.sqlcollections.aggregator.Min;

import java.util.function.Function;

public class Aggregates {

    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new Max<>(expression);
    }

    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new Min<>(expression);
    }

    public static <C> Function<C, Integer> count(Function<C, ?> expression) {
        return new Count<>(expression);
    }

    public static <C> Function<C, Double> avg(Function<C, ? extends Number> expression) {
        return new Average<>(expression);
    }

}
