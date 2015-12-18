package library.core.model.aggregation.impl;

import library.core.model.aggregation.AggregateFunction;
import library.core.utils.NumberUtils;

import java.util.function.Function;

public class AverageFunction<S, R extends Number> extends AggregateFunction<S, R, R> {

    public AverageFunction(Function<S, R> singleFunction) {
        super(singleFunction);
    }

    @Override
    public final R apply(Iterable<S> elements) {
        long count = 0L;
        Number sum = 0;
        for (S element : elements) {
            count++;
            sum = NumberUtils.SUM_NUMBERS.apply(apply(element), sum);
        }
        return (R) NumberUtils.DIV_NUMBERS.apply(sum, count);
    }
}
