package library.core.operations.impl;

import library.core.operations.OperationType;
import library.core.operations.QueryOperation;
import library.core.QueryContext;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HavingOperation implements QueryOperation {

    @Override
    public final OperationType getType() {
        return OperationType.HAVING_OP;
    }

    @Override
    public final  <R, S> void execute(QueryContext<R, S> queryContext) {
        // filter result list by using having predicate
        Predicate<R> havingCondition = queryContext.getHaving();
        List<R> filteredResult = queryContext.getResult().stream().
                filter(havingCondition).
                collect(Collectors.toList());
        // set filtered result
        queryContext.setResult(filteredResult);
    }
}
