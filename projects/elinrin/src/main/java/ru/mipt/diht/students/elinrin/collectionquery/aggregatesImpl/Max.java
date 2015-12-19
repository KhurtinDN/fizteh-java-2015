package ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl;

import java.util.List;
import java.util.function.Function;

public class Max<T, R extends Comparable<R>> implements Aggregator<T, R> {

    private Function<T, R> function;
    public Max(final Function<T, R> expression) {
        function = expression;
    }

    @Override
    public final R apply(final List<T> elements) {
        if (elements.isEmpty()) {
            return null;
        }
        T result = elements.get(0);
        for (T element : elements) {
            if (function.apply(result).compareTo(function.apply(element)) < 0) {
                result = element;
            }
        }
        return function.apply(result);
    }

    @Override
    public final R apply(final T t) {
        return null;
    }
}
