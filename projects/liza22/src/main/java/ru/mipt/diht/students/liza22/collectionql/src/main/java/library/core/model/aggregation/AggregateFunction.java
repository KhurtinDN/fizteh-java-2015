package library.core.model.aggregation;

import java.util.Objects;
import java.util.function.Function;

/**
 * Aggregate function.
 * @param <S> source element type
 * @param <R> result of single function transformation type
 * @param <A> aggregated result type
 */
public abstract class AggregateFunction<S, R, A> implements Function<S, R> {
    private Function<S, R> singleFunction;

    public AggregateFunction(Function<S, R> singleFunction1) {
        Objects.requireNonNull(singleFunction1);
        this.singleFunction = singleFunction1;
    }

    /**
     * Because function is aggregate, this applies on many elements, e.g. collection.
     * @param elements set of elements to be applied by this aggregate function
     * @return aggregation result
     */
    public abstract A apply(Iterable<S> elements);

    @Override
    public final R apply(S t) {
        // delegate single element transformation
        return singleFunction.apply(t);
    }

    @Override
    public final  <V> Function<V, R> compose(Function<? super V, ? extends S> before) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final  <V> Function<S, V> andThen(Function<? super R, ? extends V> after) {
        throw new UnsupportedOperationException();
    }
}
