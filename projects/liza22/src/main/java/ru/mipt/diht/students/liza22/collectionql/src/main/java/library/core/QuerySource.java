package library.core;

import library.api.Query;
import library.api.Source;
import library.core.model.SelectArgument;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class represents the head point of query building.
 *
 * To start build query a user must create QuerySource object
 * and invoke select[Distinct] method to get Query object.
 *
 * When select[Distinct] method invoked, it creates new
 * QueryContext and Query object, fills it by "select" statement
 * parameters and return Query instance to requester.
 *
 * @param <S> source element type
 */
public final class QuerySource<S> implements Source<S> {
    private List<S> sourceList;

    public QuerySource(List<S> sourceList1) {
        Objects.requireNonNull(sourceList1);
        if (sourceList1.isEmpty()) {
            throw new IllegalArgumentException("Source collection must not be null");
        }
        this.sourceList = sourceList1;
    }

    @Override
    public <R> Query<R, S> select(Class<R> resultClass, Function<S, ?>... arguments) {
        QueryContext<R, S> queryContext = buildQueryContext(resultClass, arguments);
        queryContext.setDistinct(false);
        return new QueryImpl<>(queryContext);
    }

    @Override
    public <R> Query<R, S> selectDistinct(Class<R> resultClass, Function<S, ?>... arguments) {
        QueryContext<R, S> queryContext = buildQueryContext(resultClass, arguments);
        queryContext.setDistinct(true);
        return new QueryImpl<>(queryContext);
    }

    private <R> QueryContext<R, S> buildQueryContext(Class<R> resultClass, Function<S, ?>... arguments) {
        Objects.requireNonNull(resultClass);
        if (arguments.length == 0) {
            throw new IllegalArgumentException("The list of select arguments must not be empty");
        }
        QueryContext<R, S> queryContext = new QueryContext<>();
        queryContext.setSource(sourceList);
        queryContext.setResultClass(resultClass);

        // create SelectArgument objects and store in sorted list
        List<SelectArgument<S>> selectArguments =
                IntStream.range(0, arguments.length)
                        .mapToObj(i -> new SelectArgument<>(i, arguments[i]))
                        .sorted((a1, a2) -> (Integer.compare(a1.getOrder(), a2.getOrder())))
                        .collect(Collectors.toList());
        queryContext.setSelectArguments(Collections.unmodifiableList(selectArguments));
        return queryContext;
    }
}