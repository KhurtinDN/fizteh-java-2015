package library.core.operations;

import library.core.operations.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for QueryOperation instances.
 */
public class QueryOperationFactory {
    private static final Map<OperationType, QueryOperation> OPERATIONINSTANCES;
    static {
        OPERATIONINSTANCES = new HashMap<>();
        OPERATIONINSTANCES.put(OperationType.WHERE_OP, new WhereOperation());
        OPERATIONINSTANCES.put(OperationType.SIMPLE_SELECT_OP, new SimpleSelectOperation());
        OPERATIONINSTANCES.put(OperationType.GROUPING_SELECT_OP, new GroupingSelectOperation());
        OPERATIONINSTANCES.put(OperationType.HAVING_OP, new HavingOperation());
        OPERATIONINSTANCES.put(OperationType.ORDER_BY_OP, new OrderByOperation());
        OPERATIONINSTANCES.put(OperationType.LIMIT_OP, new LimitOperation());
        OPERATIONINSTANCES.put(OperationType.DISTINCT_OP, new DistinctOperation());
        OPERATIONINSTANCES.put(OperationType.UNION_OP, new UnionOperation());
    }

    /**
     * @param type requested query operation type
     * @return query operation instance by requested type
     */
    public static QueryOperation getOperationByType(OperationType type) {
        return OPERATIONINSTANCES.get(type);
    }
}
