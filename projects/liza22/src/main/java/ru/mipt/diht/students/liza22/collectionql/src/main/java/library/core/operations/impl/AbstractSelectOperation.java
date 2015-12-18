package library.core.operations.impl;

import library.core.model.SelectArgument;
import library.core.model.aggregation.AggregateFunction;
import library.core.operations.QueryOperation;
import library.core.QueryContext;
import library.core.exceptions.IncorrectQueryException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract SelectOperation class which contains main functionality to
 * do select statement.
 * This class has a common part of SimpleSelect and GroupingSelect
 * operations.
 */
public abstract class AbstractSelectOperation implements QueryOperation {

    @Override
    public  <R, S> void validate(QueryContext<R, S> queryContext) throws IncorrectQueryException {
        List<SelectArgument<S>> selectArguments = queryContext.getSelectArguments();
        if (selectArguments == null || selectArguments.isEmpty()) {
            throw new IncorrectQueryException("Select statement must have arguments");
        }
        for (SelectArgument argument : selectArguments) {
            if (argument.getFunction() == null) {
                throw new IncorrectQueryException("Select arguments are incorrect");
            }
        }
    }

    protected  <S, R> List<R> doSelect(List<S> sourceElements, QueryContext<R, S> queryContext)
            throws IncorrectQueryException {
        List<SelectArgument<S>> selectArguments = queryContext.getSelectArguments();
        List<SelectArgument<S>> notAggregateArgs = selectArguments.stream().
                filter(a -> !a.isAggregate()).
                collect(Collectors.toList());
        List<SelectArgument<S>> aggregateArgs = selectArguments.stream().
                filter(a -> a.isAggregate()).
                collect(Collectors.toList());

        // Step#1: calculate value for each select argument
        calculateAggregateArguments(sourceElements, aggregateArgs);
        calculateNotAggregateArguments(sourceElements, notAggregateArgs);

        // Step#2: create result class instances by using calculated
        // in step#1 values of select arguments
        Class<R> resultClass = queryContext.getResultClass();
        Class<?>[] constructorArgTypes = getConstructorParameterTypes(selectArguments);
        try {
            Constructor<R> resultClassConstructor = resultClass.getDeclaredConstructor(constructorArgTypes);
            List<R> results = null;
            if (!notAggregateArgs.isEmpty() && aggregateArgs.isEmpty()) {
                // simple select without aggregations and grouping
                // just calculate all select arguments for each source element
                results = calculateResult(sourceElements, resultClassConstructor, notAggregateArgs);
            }
            if (notAggregateArgs.isEmpty() && !aggregateArgs.isEmpty()) {
                // exceptional case when all select arguments are aggregate functions
                // and no any grouping, so result will be just one row
                R result = calculateOnlyAggregatedResult(resultClassConstructor, aggregateArgs);
                results = Collections.singletonList(result);
            } else if (!notAggregateArgs.isEmpty() && !aggregateArgs.isEmpty()) {
                // case when aggregate function in arguments exist
                // and not-aggregate also - suppose that they are grouping arguments
                // suppose that source elements belong to only one group
                // thus we can calculate all results and get first row, because
                // the other will be the same
                R result = calculateMixedResult(sourceElements.get(0), resultClassConstructor, selectArguments);
                results = Collections.singletonList(result);
            }
            return results;
        } catch (NoSuchMethodException e) {
            throw new IncorrectQueryException("Constructor of result class = " + resultClass
                    + " with arg types = " + Arrays.toString(constructorArgTypes)
                    + " not found");
        }
    }

    protected  <S> void calculateNotAggregateArguments(List<S> sourceElements,
                                                       List<SelectArgument<S>> selectArguments) {
        for (SelectArgument<S> selectArgument : selectArguments) {
            // apply function for each element of source and store to map [element <-> value]
            Map<S, Object> elementValues = new HashMap<>(sourceElements.size());
            for (S element : sourceElements) {
                Object value = selectArgument.getFunction().apply(element);
                elementValues.put(element, value);
                if (selectArgument.getValueClazz() == null) {
                    selectArgument.setValueClazz(value.getClass());
                }
            }
            selectArgument.setValues(elementValues);
        }
    }

    protected  <S> void calculateAggregateArguments(List<S> sourceElements, List<SelectArgument<S>> selectArguments) {
        for (SelectArgument<S> selectArgument : selectArguments) {
            AggregateFunction<S, Object, Object> aggregateFunction = selectArgument.getAggregateFunction();
            Object aggregateValue = aggregateFunction.apply(sourceElements);
            selectArgument.setAggregateValue(aggregateValue);
            selectArgument.setValueClazz(aggregateValue.getClass());
        }
    }

    protected <R, S> List<R> calculateResult(List<S> sourceElements, Constructor<R> resultClassConstructor,
                                             List<SelectArgument<S>> selectArguments) throws IncorrectQueryException {
        try {
            List<R> results = new ArrayList<>(sourceElements.size());
            for (S sourceElement : sourceElements) {
                R resultElement = convertToResultObject(resultClassConstructor, sourceElement, selectArguments);
                results.add(resultElement);
            }
            return results;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IncorrectQueryException("Cannot instantiate result class instance: " + e.getMessage());
        }
    }

    protected <R, S> R calculateOnlyAggregatedResult(Constructor<R> resultClassConstructor,
                                                     List<SelectArgument<S>> selectArguments)
            throws IncorrectQueryException {
        try {
            return convertToResultObject(resultClassConstructor, null, selectArguments);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IncorrectQueryException("Cannot instantiate result class instance: " + e.getMessage());
        }
    }

    protected  <R, S> R calculateMixedResult(S sourceElement, Constructor<R> resultClassConstructor,
                                             List<SelectArgument<S>> selectArguments) throws IncorrectQueryException {
        try {
            return convertToResultObject(resultClassConstructor, sourceElement, selectArguments);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IncorrectQueryException("Cannot instantiate result class instance: " + e.getMessage());
        }
    }

    protected static <S> Class<?>[] getConstructorParameterTypes(List<SelectArgument<S>> arguments) {
        Class<?>[] types = new Class[arguments.size()];
        // arguments are supposed to be sorted by order
        for (int i = 0; i < arguments.size(); i++) {
            types[i] = arguments.get(i).getValueClazz();
        }
        return types;
    }

    protected static <S, R> R convertToResultObject(Constructor<R> resultConstructor, S source,
                                                    List<SelectArgument<S>> selectArguments)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] constructorArguments = new Object[selectArguments.size()];
        for (int i = 0; i < selectArguments.size(); i++) {
            SelectArgument<S> selectArgument = selectArguments.get(i);
            Object value = (selectArgument.isAggregate())
                    ? selectArgument.getAggregatedValue()
                    : selectArgument.getValues().get(source);
            constructorArguments[i] = value;
        }
        return resultConstructor.newInstance(constructorArguments);
    }
}
