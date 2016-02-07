package ru.mipt.diht.students.collectionquery;

import java.util.List;
import java.util.stream.Collector;

/**
 * Created by mikhail on 01.02.16.
 */
public class AggregateFunctionImpl<C, T> implements AggregateFunction<C, T> {
    private final Collector<C, ?, T> collector;

    AggregateFunctionImpl(Collector<C, ?, T> collector) {
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
