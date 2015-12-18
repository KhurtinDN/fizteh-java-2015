package ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl;

import java.util.List;
import java.util.function.Function;

public class Avg<T> implements Aggregator<T, Double> {

    private Function<T, ? extends Number> function;
    public Avg(final Function<T, ? extends Number> expression) {
        this.function = expression;
    }

    @Override
    public final Double apply(final List<T> elements) {
        Double result = 0.0;
        for (T element : elements) {
            result += (Double) function.apply(element);
        }
        return result / elements.size();
    }

    @Override
    public final Double apply(final T t) {
        return null;
    }
}
