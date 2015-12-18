package library.core.operations.impl;

import library.core.operations.OperationType;
import library.core.operations.QueryOperation;
import library.core.QueryContext;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class WhereOperation implements QueryOperation {

    @Override
    public OperationType getType() {
        return OperationType.WHERE_OP;
    }

    @Override
    public <R, S> void execute(QueryContext<R, S> queryContext) {
        Predicate<S> whereCondition = queryContext.getWhere();
        // filter source list by using predicate from "where" statement
        List<S> filteredSource = queryContext.getSource().stream().
                filter(whereCondition).
                collect(Collectors.toList());
        // set filtered source list to context
        queryContext.setSource(filteredSource);
    }
}