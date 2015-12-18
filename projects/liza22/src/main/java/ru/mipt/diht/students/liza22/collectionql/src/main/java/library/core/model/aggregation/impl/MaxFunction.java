package library.core.model.aggregation.impl;

import library.core.utils.NumberUtils;

import java.util.List;
import java.util.function.Function;

public class MaxFunction<S, R extends Number> extends ExtremumFunction<S, R> {

    public MaxFunction(Function<S, R> singleFunction) {
        super(singleFunction);
    }

    @Override
    protected final R findExtremum(List<R> values) {
        return values.stream().max(NumberUtils.COMPARE_NUMBERS::apply).get();
    }
}
