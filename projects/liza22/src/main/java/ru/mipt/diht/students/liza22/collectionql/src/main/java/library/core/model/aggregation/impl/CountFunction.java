package library.core.model.aggregation.impl;

import library.core.model.aggregation.AggregateFunction;

import java.util.function.Function;
import java.util.stream.StreamSupport;

public class CountFunction<S, R> extends AggregateFunction<S, R, Long> {

    public CountFunction(Function<S, R> singleFunction) {
        super(singleFunction);
    }

    @Override
    public final Long apply(Iterable<S> elements) {
        // start stream, filter only not null elements and count them
        return StreamSupport.stream(elements.spliterator(), false).
                filter(e -> e != null).
                count();
    }
}
