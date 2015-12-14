package ru.mipt.diht.students.simon23rus.CQL.implOfAggregators;


import java.util.List;
import java.util.function.Function;

public interface Aggregator<T, C> extends Function<T, C> {
    default C apply(List<T> currentElements) {
        return null;
    }
}

