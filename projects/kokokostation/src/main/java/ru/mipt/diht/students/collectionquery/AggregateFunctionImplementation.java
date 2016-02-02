package ru.mipt.diht.students.collectionquery;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Created by mikhail on 01.02.16.
 */
public class AggregateFunctionImplementation<C, T> implements AggregateFunction<C, T> {
    private final Collector<C, ?, T> collector;

    AggregateFunctionImplementation(Collector<C, ?, T> collector) {
        this.collector = collector;
    }

    @Override
    public T apply(List<C> item) {
        return item.stream().collect(collector);
    }

    @Override
    public T apply(C c) {
        return null;
    }
}