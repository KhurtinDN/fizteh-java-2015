package ru.mipt.diht.students.maxdankow.sqlcollections.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Count<T> implements Aggregator<T, Integer> {

    private Function<T, ?> function;

    public Count(Function<T, ?> newFunction) {
        function = newFunction;
    }

    @Override
    public final Integer apply(List<T> list) {
        Integer count = 0;
        Set<Object> distinctElements = new HashSet<>();

        for (T element : list) {
            if (!distinctElements.contains(function.apply(element))) {
                distinctElements.add(function.apply(element));
            }
        }
        return distinctElements.size();
    }

    @Override
    public final Integer apply(T t) {
        return null;
    }
}
