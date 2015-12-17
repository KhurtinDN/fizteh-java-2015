package ru.mipt.diht.students.maxDankow.sqlcollections.aggregator;

import java.util.List;
import java.util.function.Function;

public class Average<T> implements Aggregator<T, Double> {

    private Function<T, ? extends Number> function;

    public Average(Function<T, ? extends Number> newFunction) {
        function = newFunction;
    }

    @Override
    public final Double apply(List<T> list) {
        Double sum = 0.0;

        for (T element : list) {
            sum += function.apply(element).doubleValue();
        }
        return sum / list.size();
    }

    @Override
    public final Double apply(T t) {
        return null;
    }
}