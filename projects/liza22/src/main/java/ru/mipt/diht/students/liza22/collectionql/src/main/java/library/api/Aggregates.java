package library.api;

import library.core.model.aggregation.AggregateFunction;
import library.core.model.aggregation.impl.AverageFunction;
import library.core.model.aggregation.impl.CountFunction;
import library.core.model.aggregation.impl.MaxFunction;
import library.core.model.aggregation.impl.MinFunction;

import java.util.function.Function;

/**
 * Aggregate functions.
 */
public class Aggregates {

    public static <S, R> AggregateFunction<S, R, Long> count(Function<S, R> countingFunction) {
        return new CountFunction<>(countingFunction);
    }

    public static <S, R extends Number> AggregateFunction<S, R, R> avg(Function<S, R> averageFunction) {
        return new AverageFunction<>(averageFunction);
    }

    public static <S, R extends Number> AggregateFunction<S, R, R> max(Function<S, R> function) {
        return new MaxFunction<>(function);
    }

    public static <S, R extends Number> AggregateFunction<S, R, R> min(Function<S, R> function) {
        return new MinFunction<>(function);
    }

    /**
     * This function represents AggregateFunction stub and
     * can be used in aggregate select statement.
     * Provides always the same value - constant argument.
     *
     * @param constant to provide value
     * @param <S> source element type
     * @param <R> result element type
     * @return function stub for constant value
     */
    public static <S, R> AggregateFunction<S, R, R> constant(R constant) {
        return new AggregateFunction<S, R, R>(e -> constant) {
            @Override
            public R apply(Iterable<S> elements) {
                return constant;
            }
        };
    }
}
