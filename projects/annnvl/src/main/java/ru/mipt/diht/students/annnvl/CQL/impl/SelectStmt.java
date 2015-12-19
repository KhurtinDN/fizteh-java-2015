package ru.mipt.diht.students.annnvl.CQL.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectStmt<T, R> {

    private boolean isDistinct;
    private Class returnClass;
    private Function[] functions;
    private List<T> elements;

    private List<R> pastElements;

    private Predicate<T> whereCondition;
    private Comparator<R>[] comparators;
    private Predicate<R> havingCondition;
    private int numberOfObjects;
    private Function<T, ?>[] groupByConditions;

    private CQLComparator<R> cqlComparator;
    private boolean isUnion;
    private boolean isJoin;

    @SafeVarargs
    public SelectStmt(List<T> elements, Class<R> returnClass, boolean isDistinct, Function<T, ?>... functions) {
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
        this.returnClass = returnClass;
        this.isDistinct = isDistinct;
        this.functions = functions;
        this.numberOfObjects = -1;
        this.isUnion = false;
        this.isJoin = false;
    }

    public SelectStmt(List<T> elements, boolean isDistinct, Function<T, ?> first, Function<T, ?> second) {
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
        this.returnClass = elements.get(0).getClass();
        this.isDistinct = isDistinct;
        this.functions = new Function[]{first, second};
        this.numberOfObjects = -1;
        this.isUnion = false;
        this.isJoin = true;
    }

    @SafeVarargs
    public SelectStmt(List<R> pastElements, List<T> elements, Class<R> returnClass,
                      boolean isDistinct, Function<T, ?>... functions) {
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
        this.returnClass = returnClass;
        this.isDistinct = isDistinct;
        this.functions = functions;
        this.numberOfObjects = -1;
        this.isUnion = true;
        this.pastElements = pastElements;
    }

    public SelectStmt(List<R> pastElements, List<T> elements, boolean isDistinct, Function<T, ?> first,
                      Function<T, ?> second) {
        this.elements = new ArrayList<>();
        this.elements.addAll(elements);
        this.returnClass = elements.get(0).getClass();
        this.isDistinct = isDistinct;
        this.functions = new Function[]{first, second};
        this.numberOfObjects = -1;
        this.isUnion = true;
        this.isJoin = true;
        this.pastElements = pastElements;
    }

    public SelectStmt<T, R> where(Predicate<T> predicate) {
        this.whereCondition = predicate;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
        groupByConditions = expressions;
        //groupByConditions = Arrays.asList(expressions);
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) {
        this.comparators = comparators;
        this.cqlComparator = new CQLComparator<R>(comparators);
        return this;
    }

    public SelectStmt<T, R> having(Predicate<R> condition) {
        this.havingCondition = condition;
        return this;
    }

    public SelectStmt<T, R> limit(int amount) {
        this.numberOfObjects = amount;
        return this;
    }

    public Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        List<R> result = new ArrayList<>();
        Class[] returnClasses = new Class[functions.length];
        if (whereCondition != null) {
            elements = elements
                    .stream()
                    .filter(whereCondition::test)
                    .collect(Collectors.toList());
        }
        if (groupByConditions != null) {

            Map<Integer, List<T>> groupedMap = elements
                    .stream()
                    .collect(Collectors.groupingBy((T element) -> {
                        List<Object> answer = new ArrayList<>();
                        for (int i = 0; i < groupByConditions.length; ++i) {
                            answer.add(groupByConditions[i].apply(element));
                        }
/*
                        List<Object> answer = groupByConditions
                                .stream()
                                .map(cond -> cond.apply(element))
                                .collect(Collectors.toList());
*/
                        return answer.hashCode();
                    }));

            List<List<T>> groupedList = groupedMap
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            Object[] arguments = new Object[functions.length];
            for (List<T> group : groupedList) {
                for (int i = 0; i < functions.length; i++) {
                    if (functions[i] instanceof  Aggregator) {
                        arguments[i] = ((Aggregator) functions[i]).apply(group);
                    } else {
                        arguments[i] = functions[i].apply(group.get(0));
                    }
                    returnClasses[i] = arguments[i].getClass();
                }
                if (isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    result.add((R) newElement);
                } else {
                    R newElement = (R) returnClass.getConstructor(returnClasses).newInstance(arguments);
                    result.add(newElement);
                }
            }
        } else {
            Object[] arguments = new Object[functions.length];
            for (T element : this.elements) {
                for (int i = 0; i < functions.length; i++) {
                    arguments[i] = functions[i].apply(element);
                    if (functions[i] instanceof  Aggregator) {
                        List<T> currArg = new ArrayList<>();
                        currArg.add(element);
                        arguments[i] = ((Aggregator) functions[i]).apply(currArg);
                    } else {
                        arguments[i] = functions[i].apply(element);
                    }
                    returnClasses[i] = arguments[i].getClass();
                }
                if (isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    result.add((R) newElement);
                } else {
                    R newElement = (R) returnClass.getConstructor(returnClasses).newInstance(arguments);
                    result.add(newElement);
                }
            }
        }
        Stream<R> resultStream = result.stream();
        if (havingCondition != null) {
            resultStream = resultStream.filter(havingCondition::test);
        }
        if (isDistinct) {
            resultStream = resultStream.distinct();
        }
        if (comparators != null) {
            resultStream = resultStream.sorted(cqlComparator);
        }
        if (numberOfObjects != -1) {
            resultStream = resultStream.limit(numberOfObjects);
        }
        result = resultStream.collect(Collectors.toList());
        if (isUnion) {
            pastElements.addAll(result);
            result = pastElements;
        }
        return result;
    }

    public UnionStmt<T, R> union() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        List<R> result = (List<R>) this.execute();
        if (isJoin) {
            return new UnionStmt<>(result, true);
        } else {
            return new UnionStmt<>(result);
        }
    }

    public class CQLComparator<K> implements Comparator<K> {
        private Comparator<K>[] comparators;
        @SafeVarargs
        public CQLComparator(Comparator<K>... comparators) {
            this.comparators = comparators;
        }

        @Override
        public int compare(K first, K second) {
            for (Comparator<K> comparator : comparators) {
                if (comparator.compare(first, second) != 0) {
                    return comparator.compare(first, second);
                }
            }
            return 0;
        }
    }
}
