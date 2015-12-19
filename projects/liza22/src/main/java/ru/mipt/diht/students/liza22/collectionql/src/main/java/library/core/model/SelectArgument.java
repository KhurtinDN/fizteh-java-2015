package library.core.model;

import library.core.model.aggregation.AggregateFunction;

import java.util.Map;
import java.util.function.Function;

/**
 * Class wraps and keeps full information about a select argument in query.
 * @param <S> source element type
 */
public class SelectArgument<S> {
    /**
     * Order of this argument in query.
     */
    private int order;
    /**
     * Transformation function - lambda or aggregate function.
     */
    private Function<S, ?> function;
    /**
     * Argument value in case of argument is aggregate.
     */
    private Object aggregatedValue;
    /**
     * Argument values mapped to source elements (rows) in case of argument is not aggregate.
     */
    private Map<S, ?> values;
    /**
     * Class type of select argument.
     * Used to find result class constructor with required signature.
     */
    private Class<?> valueClazz;

    public SelectArgument(int order1, Function<S, ?> function1) {
        this.order = order1;
        this.function = function1;
    }

    public final int getOrder() {
        return order;
    }

    public final Function<S, ?> getFunction() {
        return function;
    }

    public final Object getAggregatedValue() {
        return aggregatedValue;
    }

    public final void setAggregateValue(Object aggregatedValue1) {
        this.aggregatedValue = aggregatedValue1;
    }

    public final Map<S, ?> getValues() {
        return values;
    }

    public final void setValues(Map<S, ?> values1) {
        this.values = values1;
    }

    public final boolean isAggregate() {
        return (function instanceof AggregateFunction);
    }

    public final  <R, A> AggregateFunction<S, R, A> getAggregateFunction() {
        if (isAggregate()) {
            return (AggregateFunction<S, R, A>) function;
        } else {
            throw new IllegalStateException("This argument is not aggregated");
        }
    }

    public final Class<?> getValueClazz() {
        return valueClazz;
    }

    public final void setValueClazz(Class<?> valueClazz1) {
        this.valueClazz = valueClazz1;
    }
}
