package library.core.operations.impl;

import library.core.operations.OperationType;
import library.core.operations.QueryOperation;
import library.core.QueryContext;
import org.apache.commons.collections4.comparators.ComparatorChain;

import java.util.Collections;
import java.util.Comparator;

public class OrderByOperation implements QueryOperation {

    @Override
    public final OperationType getType() {
        return OperationType.ORDER_BY_OP;
    }

    @Override
    public final  <R, S> void execute(QueryContext<R, S> queryContext) {
        // Apache ComparatorChain used to create chain of comparators
        // and order by this one
        ComparatorChain<R> comparatorChain = new ComparatorChain<>();
        for (Comparator<R> orderByComparator : queryContext.getOrderBy()) {
            comparatorChain.addComparator(orderByComparator);
        }
        Collections.sort(queryContext.getResult(), comparatorChain);
    }
}