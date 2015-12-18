package library.core.operations;

import library.core.operations.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for QueryOperation instances.
 */
public class QueryOperationFactory {
    private static final Map<OperationType, QueryOperation> operationInstances;
    static {
        operationInstances = new HashMap<>();
        operationInstances.put(OperationType.WHERE_OP, new WhereOperation());
        operationInstances.put(OperationType.SIMPLE_SELECT_OP, new SimpleSelectOperation());
        operationInstances.put(OperationType.GROUPING_SELECT_OP, new GroupingSelectOperation());
        operationInstances.put(OperationType.HAVING_OP, new HavingOperation());
        operationInstances.put(OperationType.ORDER_BY_OP, new OrderByOperation());
        operationInstances.put(OperationType.LIMIT_OP, new LimitOperation());
        operationInstances.put(OperationType.DISTINCT_OP, new DistinctOperation());
        operationInstances.put(OperationType.UNION_OP, new UnionOperation());
    }

    /**
     * @param type requested query operation type
     * @return query operation instance by requested type
     */
    public static QueryOperation getOperationByType(OperationType type) {
        return operationInstances.get(type);
    }
}