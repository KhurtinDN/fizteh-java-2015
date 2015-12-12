package ru.mipt.diht.students.alokotok.collectionquery;

import ru.mipt.diht.students.alokotok.collectionquery.impl.Avg;
import ru.mipt.diht.students.alokotok.collectionquery.impl.Count;
import ru.mipt.diht.students.alokotok.collectionquery.impl.Max;
import ru.mipt.diht.students.alokotok.collectionquery.impl.Min;

import java.util.function.Function;

/**
 * Created by lokotochek on 30.11.15.
 */
public class Aggregates {
    public static <T, R extends Comparable> Function<T, R> max(Function<T, R> expression) {
        return new Max<>(expression);
    }

    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new Min<>(expression);
    }

    public static <T> Function<T, Integer> count(Function<T, ?> expression) {
        return new Count<>(expression);
    }

    public static <T> Function<T, Double> avg(Function<T, ? extends Number> expression) {
        return new Avg<>(expression);
    }

}