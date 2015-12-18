package library.core.operations.impl;

import library.api.Query;
import library.core.operations.OperationType;
import library.core.QueryContext;
import library.core.exceptions.IncorrectQueryException;
import library.core.operations.QueryOperation;

import java.util.LinkedList;
import java.util.List;

public final class UnionOperation implements QueryOperation {

    @Override
    public OperationType getType() {
        return OperationType.UNION_OP;
    }

    @Override
    public <R, S> void execute(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        for (Query<R, S> unionQuery : queryContext.getUnions()) {
            // execute union query, get its result list and put it into linked list
            List<R> unionQueryResult = new LinkedList<>();
            unionQuery.execute().forEach(unionQueryResult::add);
            // after that this result is "glued" to our current result
            queryContext.addResult(unionQueryResult);
        }
    }
}