package ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Count<T> implements Aggregator<T, Integer> {

    private Function<T, ?> function;
    public Count(final Function<T, ?> expression) {
        this.function = expression;
    }

    @Override
    public final Integer apply(final List<T> elements) {
        Set<Object> distincted = new HashSet<>();
        for (T element : elements) {
            if (!distincted.contains(function.apply(element))) {
                distincted.add(function.apply(element));
            }
        }
        return distincted.size();
    }
    @Override
    public final Integer apply(final T t) {
        return null;
    }
}
