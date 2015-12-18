package library.core.model;

import java.util.Map;
import java.util.function.Function;

/**
 * Class wraps and keeps full information about a grouping condition in query's groupBy statement.
 * @param <S> source element type
 */
public class GroupingCondition<S> {
    /**
     * Order of this condition.
     */
    private int order;
    /**
     * Transformation function - labmda or aggregate function.
     */
    private Function<S, ?> function;
    /**
     * Calculated values of this function for each source element (rows).
     * These values are used to create groups of elements (rows).
     */
    private Map<S, Object> groupedValues;

    public GroupingCondition(int order1, Function<S, ?> function1) {
        this.order = order1;
        this.function = function1;
    }

    public final int getOrder() {
        return order;
    }

    public final Function<S, ?> getFunction() {
        return function;
    }

    public final Map<S, Object> getGroupedValues() {
        return groupedValues;
    }

    public final void setGroupedValues(Map<S, Object> groupedValues1) {
        this.groupedValues = groupedValues1;
    }
}