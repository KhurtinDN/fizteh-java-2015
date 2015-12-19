package library.core.operations;

import library.core.QueryContext;
import library.core.exceptions.IncorrectQueryException;

import java.util.Comparator;

/**
 * Class represents one query operation, such as "select", "orderBy" and etc.
 */
public interface QueryOperation {

    /**
     * Comparator to compare QueryOperation instances by their order.
     */
    Comparator<QueryOperation> ORDER_COMPARATOR =
            (o1, o2) -> Integer.compare(o1.getType().getOrder(), o2.getType().getOrder());

    /**
     * @return type of query operation
     */
    OperationType getType();

    /**
     * Validates query context before execution this operation.
     * By default do nothing.
     * @param queryContext execution query context
     * @param <R> result type
     * @param <S> source elements type
     * @throws IncorrectQueryException in case of any incorrect in query found
     */
    default <R, S> void validate(final QueryContext<R, S> queryContext) throws IncorrectQueryException {
        // by default do nothing
    }

    /**
     * Main method which executes this query operation.
     * @param queryContext execution query context
     * @param <R> result type
     * @param <S> source elements type
     * @throws IncorrectQueryException in case of any incorrect in query found
     */
    <R, S> void execute(final QueryContext<R, S> queryContext) throws IncorrectQueryException;
}
