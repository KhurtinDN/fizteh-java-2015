package library.api;

import library.core.exceptions.IncorrectQueryException;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Query builder class.
 * @param <R> result element type
 * @param <S> source element type
 */
public interface Query<R, S> {

    Query<R, S> where(Predicate<S> whereCondition);

    Query<R, S> groupBy(Function<S, ?>... groupByFunction);

    Query<R, S> having(Predicate<R> havingCondition);

    Query<R, S> orderBy(Comparator<R>... orderByComparators);

    Query<R, S> limit(int limit);

    Query<R, S> union(Query<R, S> query);

    Iterable<R> execute() throws IncorrectQueryException;
}
