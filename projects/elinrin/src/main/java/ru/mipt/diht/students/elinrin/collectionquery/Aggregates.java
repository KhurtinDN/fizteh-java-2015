package ru.mipt.diht.students.elinrin.collectionquery;

import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Avg;
import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Count;
import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Max;
import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Min;

import java.util.function.Function;

public class Aggregates {
    public static <T, R extends Comparable> Function<T, R> max(final Function<T, R> expression) {
        return new Max<>(expression);
    }

    public static <C, T extends Comparable<T>> Function<C, T> min(final Function<C, T> expression) {
        return new Min<>(expression);
    }

    public static <T> Function<T, Integer> count(final Function<T, ?> expression) {
        return new Count<>(expression);
    }

    public static <T> Function<T, Double> avg(final Function<T, ? extends Number> expression) {
        return new Avg<>(expression);
    }

}
