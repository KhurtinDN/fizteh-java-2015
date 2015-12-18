package library.core.operations.impl;

import library.core.exceptions.IncorrectQueryException;
import library.core.model.SelectArgument;
import library.core.operations.OperationType;
import library.core.QueryContext;

import java.util.List;

/**
 * This type of operation used when select query is not grouping.
 */
public class SimpleSelectOperation extends AbstractSelectOperation {

    @Override
    public final OperationType getType() {
        return OperationType.SIMPLE_SELECT_OP;
    }

    @Override
    public final  <R, S> void validate(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        //check QueryContext
        List<SelectArgument<S>> selectArguments = queryContext.getSelectArguments();
        if (selectArguments == null || selectArguments.isEmpty()) {
            throw new IncorrectQueryException("Select statement must have arguments");
        }
        for (SelectArgument argument : selectArguments) {
            if (argument.getFunction() == null) {
                throw new IncorrectQueryException("Select arguments are incorrect");
            }
        }
        // check that there is not aggregate functions at all in "select" statement
        // or if exists, then all arguments must be aggregate functions
        // because this type of query doesn't have GroupBy condition and
        // simple expressions are not permitted in "select" statement
        List<SelectArgument<S>> selectArguments = queryContext.getSelectArguments();
        long aggregateArgsCnt = selectArguments.stream().filter(SelectArgument::isAggregate).count();
        if (aggregateArgsCnt != 0 && aggregateArgsCnt != selectArguments.size()) {
            throw new IncorrectQueryException("The only grouping expressions or aggregate functions "
                    + "are permitted in select query with aggregate functions.");
        }
    }

    @Override
    public final  <R, S> void execute(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        List<S> sourceElements = queryContext.getSource();
        // in case when all rows have been filtered out
        if (sourceElements.isEmpty()) {
            return;
        }
        List<R> results = doSelect(sourceElements, queryContext);
        queryContext.addResult(results);
    }
}
