package library.core.model.aggregation.impl;

import library.core.model.aggregation.AggregateFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ExtremumFunction<S, R extends Number> extends AggregateFunction<S, R, R> {

    public ExtremumFunction(Function<S, R> singleFunction) {
        super(singleFunction);
    }

    @Override
    public final R apply(Iterable<S> elements) {
        // collect all values (results of transformation function)
        List<R> values = new ArrayList<>();
        for (S element : elements) {
            R value = this.apply(element);
            values.add(value);
        }
        // search max or min value and return this one
        return findExtremum(values);
    }

    protected abstract R findExtremum(List<R> values);
}
