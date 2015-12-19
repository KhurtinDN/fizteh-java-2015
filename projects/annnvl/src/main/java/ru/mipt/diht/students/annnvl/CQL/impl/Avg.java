package ru.mipt.diht.students.annnvl.CQL.impl;

import java.util.List;
import java.util.function.Function;

public class Avg<T> implements Aggregator<T, Double> {

    private Function<T, ? extends Number> function;
    public Avg(Function<T, ? extends Number> expression) {
        this.function = expression;
    }

    @Override
    public Double apply(List<T> elements) {
        return elements
                .stream()
                .map(function)
                .mapToDouble(element -> (Double) element)
                .average()
                .getAsDouble();
    }

    @Override
    public Double apply(T t) {
        return null;
    }
}
