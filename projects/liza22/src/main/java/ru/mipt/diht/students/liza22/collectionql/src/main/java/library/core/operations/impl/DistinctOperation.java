package library.core.operations.impl;

import library.core.operations.OperationType;
import library.core.operations.QueryOperation;
import library.core.QueryContext;

import java.util.*;

public final class DistinctOperation implements QueryOperation {

    @Override
    public OperationType getType() {
        return OperationType.DISTINCT_OP;
    }

    @Override
    public <R, S> void execute(QueryContext<R, S> queryContext) {
        // filter duplicates
        List<R> result = queryContext.getResult();
        // iterate by list of results and remove duplicated elements
        // HashSet used because it can contain the only unique elements
        Set<R> distinctElements = new HashSet<>(result.size());
        for (Iterator<R> iterator = result.iterator(); iterator.hasNext();) {
            R element = iterator.next();
            if (!distinctElements.contains(element)) {
                distinctElements.add(element);
            } else {
                iterator.remove();
            }
        }
    }
}
