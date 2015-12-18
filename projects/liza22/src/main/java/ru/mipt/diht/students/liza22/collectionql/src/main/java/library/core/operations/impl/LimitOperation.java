package library.core.operations.impl;

import library.core.QueryContext;
import library.core.operations.OperationType;
import library.core.operations.QueryOperation;

import java.util.List;
import java.util.stream.Collectors;

public class LimitOperation implements QueryOperation {

    @Override
    public final OperationType getType() {
        return OperationType.LIMIT_OP;
    }

    @Override
    public final  <R, S> void execute(QueryContext<R, S> queryContext) {
        // limit result list by using stream limit() method
        List<R> cutResult = queryContext.getResult().stream().
                limit(queryContext.getLimit()).
                collect(Collectors.toList());
        // set cut result list ot context
        queryContext.setResult(cutResult);
    }
}
