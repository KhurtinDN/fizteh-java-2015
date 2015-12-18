package ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by alex on 17.12.15.
 */
public class Avg<T> implements Aggregator<T, Double> {
    private Function<T, ? extends Number> function;

    public Avg(Function<T, ? extends Number> rcvFunction) {
        this.function = rcvFunction;
    }

    @Override
    public final Double apply(List<T> elements) {
        if (elements.size() == 0) {
            return 0D;
        }
        Double result = 0D;
        for (T elem : elements) {
            result += function.apply(elem).doubleValue();
        }
        return result / elements.size();
    }

    @Override
    public final Double apply(T elem) {
        if (function.apply(elem) != null) {
            return function.apply(elem).doubleValue();
        }
        return 0D;
    }
}
