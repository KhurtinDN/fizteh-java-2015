package library.core.operations;

import library.core.operations.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for QueryOperation instances.
 */
public class QueryOperationFactory {
    private static final Map<OperationType, QueryOperation> OperationInstances;
    static {
        OperationInstances = new HashMap<>();
        OperationInstances.put(OperationType.WHERE_OP, new WhereOperation());
        OperationInstances.put(OperationType.SIMPLE_SELECT_OP, new SimpleSelectOperation());
        OperationInstances.put(OperationType.GROUPING_SELECT_OP, new GroupingSelectOperation());
        OperationInstances.put(OperationType.HAVING_OP, new HavingOperation());
        OperationInstances.put(OperationType.ORDER_BY_OP, new OrderByOperation());
        OperationInstances.put(OperationType.LIMIT_OP, new LimitOperation());
        OperationInstances.put(OperationType.DISTINCT_OP, new DistinctOperation());
        OperationInstances.put(OperationType.UNION_OP, new UnionOperation());
    }

    /**
     * @param type requested query operation type
     * @return query operation instance by requested type
     */
    public static QueryOperation getOperationByType(OperationType type) {
        return OperationInstances.get(type);
    }
}
