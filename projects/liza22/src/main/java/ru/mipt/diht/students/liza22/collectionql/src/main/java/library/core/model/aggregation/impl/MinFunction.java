package library.core.model.aggregation.impl;

import library.core.utils.NumberUtils;

import java.util.List;
import java.util.function.Function;

public class MinFunction<S, R extends Number> extends ExtremumFunction<S, R> {

    public MinFunction(Function<S, R> singleFunction) {
        super(singleFunction);
    }

    @Override
    protected final R findExtremum(List<R> values) {
        return values.stream().min(NumberUtils.COMPARE_NUMBERS::apply).get();
    }
}
