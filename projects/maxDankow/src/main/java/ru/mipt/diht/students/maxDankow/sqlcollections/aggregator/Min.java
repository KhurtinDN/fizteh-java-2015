package ru.mipt.diht.students.maxDankow.sqlcollections.aggregator;

import java.util.List;
import java.util.function.Function;

public class Min<T, R extends Comparable<R>> implements Aggregator<T, R> {

    private Function<T, R> function;

    public Min(Function<T, R> newFunction) {
        function = newFunction;
    }

    @Override
    public final R apply(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        T maxElement = list.get(0);

        for (T element : list) {
            if (function.apply(element).compareTo(function.apply(maxElement)) < 0) {
                maxElement = element;
            }
        }
        return function.apply(maxElement);
    }

    @Override
    public final R apply(T t) {
        return null;
    }
}