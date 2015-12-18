package library.core;

import library.api.Query;
import library.core.exceptions.IncorrectQueryException;
import library.core.model.GroupingCondition;
import library.core.operations.OperationType;
import library.core.operations.QueryOperation;
import library.core.operations.QueryOperationFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class implements all functionality of Query interface.
 *
 * The main idea is:
 * for each query statement the arguments are set to QueryContext
 * and the corresponding operation is included in QueryOperationsChain.
 *
 * When method "execute" invoked all operations are ordered by
 * their priority and executed bit by bit by using common Query context.
 *
 * @param <R> result element type
 * @param <S> source element type
 */
public final class QueryImpl<R, S> implements Query<R, S> {
    private final QueryContext<R, S> queryContext;
    private final List<QueryOperation> queryOperationsChain;

    public QueryImpl(QueryContext<R, S> queryContext1) {
        Objects.requireNonNull(queryContext1);
        this.queryContext = queryContext1;
        this.queryOperationsChain = new LinkedList<>();
    }

    @Override
    public Query<R, S> where(Predicate<S> whereCondition) {
        Objects.requireNonNull(whereCondition);
        queryContext.setWhere(whereCondition);
        queryOperationsChain.add(QueryOperationFactory.getOperationByType(OperationType.WHERE_OP));
        return this;
    }

    @Override
    public Query<R, S> groupBy(Function<S, ?>... groupByConditions) {
        if (groupByConditions.length == 0) {
            throw new IllegalArgumentException("GroupBy statement without condition(-s)");
        }
        List<GroupingCondition<S>> groupingConditions =
                IntStream.range(0, groupByConditions.length)
                        .mapToObj(i -> new GroupingCondition<>(i, groupByConditions[i]))
                        .sorted((a1, a2) -> (Integer.compare(a1.getOrder(), a2.getOrder())))
                        .collect(Collectors.toList());
        queryContext.setGroupingConditions(groupingConditions);
        return this;
    }

    @Override
    public Query<R, S> having(Predicate<R> havingCondition) {
        Objects.requireNonNull(havingCondition);
        queryContext.setHaving(havingCondition);
        queryOperationsChain.add(QueryOperationFactory.getOperationByType(OperationType.HAVING_OP));
        return this;
    }

    @Override
    public Query<R, S> orderBy(Comparator<R>... orderByComparators) {
        if (orderByComparators.length == 0) {
            throw new IllegalArgumentException("OrderBy statement without comparator(-s)");
        }
        queryContext.setOrderBy(orderByComparators);
        queryOperationsChain.add(QueryOperationFactory.getOperationByType(OperationType.ORDER_BY_OP));
        return this;
    }

    @Override
    public Query<R, S> limit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        queryContext.setLimit(limit);
        queryOperationsChain.add(QueryOperationFactory.getOperationByType(OperationType.LIMIT_OP));
        return this;
    }

    @Override
    public Query<R, S> union(Query<R, S> query) {
        Objects.requireNonNull(query);
        queryContext.addUnion(query);
        QueryOperation unionOp = QueryOperationFactory.getOperationByType(OperationType.UNION_OP);
        if (!queryOperationsChain.contains(unionOp)) {
            queryOperationsChain.add(unionOp);
        }
        return this;
    }

    @Override
    public Iterable<R> execute() throws IncorrectQueryException {
        // here will be fun !

        // add select operation to chain of query operations
        if (queryContext.isGroupingQuery()) {
            QueryOperation selectOperation = QueryOperationFactory.getOperationByType(OperationType.GROUPING_SELECT_OP);
        } else {
            QueryOperation selectOperation = QueryOperationFactory.getOperationByType(OperationType.SIMPLE_SELECT_OP);
        }

        queryOperationsChain.add(selectOperation);
        if (queryContext.isDistinct()) {
            queryOperationsChain.add(QueryOperationFactory.getOperationByType(OperationType.DISTINCT_OP));
        }

        // order operations by their order number
        Collections.sort(queryOperationsChain, QueryOperation.ORDER_COMPARATOR);
        // validate every statement before execution
        for (QueryOperation op : queryOperationsChain) {
            op.validate(queryContext);
        }
        // execute every operation step by step
        for (QueryOperation op : queryOperationsChain) {
            op.execute(queryContext);
        }
        return queryContext.getResult();
    }
}
