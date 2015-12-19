package ru.mipt.diht.students.annnvl.CQL.impl;

import java.util.List;
import java.util.function.Function;

public class Count<T> implements Aggregator<T, Integer> {

    private Function<T, ?> function;
    public Count(Function<T, ?> expression) {
        this.function = expression;
    }

    @Override
    public Integer apply(List<T> elements) {
        Long longAns =  elements
                .stream()
                .map(function)
                .distinct()
                .count();
        return longAns.intValue();
    }
    @Override
    public Integer apply(T t) {
        return null;
    }
}
