package ru.mipt.diht.students.elinrin.collectionquery.impl;

import javafx.util.Pair;
import ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStmt<T, R> {

    public final boolean isDistinct() {
        return isDistinct;
    }

    public final Function[] getFunctions() {
        return functions;
    }

    public final Class getReturnClass() {
        return returnClass;
    }

    public final int getNumberOfObjects() {
        return numberOfObjects;
    }

    public final boolean isUnion() {
        return isUnion;
    }

    public final List<T> getElements() {
        return elements;
    }

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
    public SelectStmt(final List<T> gottenElements, final Class<R> gottenReturnClass, final boolean gottenIsDistinct,
                      final Function<T, ?>... gottenFunctions) {
        elements = new ArrayList<>();
        for (T element : gottenElements) {
            //System.out.println(element.toString());
            elements.add(element);
        }
        returnClass = gottenReturnClass;
        isDistinct = gottenIsDistinct;
        functions = gottenFunctions;
        numberOfObjects = -1;
        isUnion = false;
        isJoin = false;
    }

    public SelectStmt(final List<T> gottenElements, final boolean gottenIsDistinct, final Function<T, ?> first,
                      final Function<T, ?> second) {
        elements = new ArrayList<>();
        for (T element : gottenElements) {
            //System.out.println(gottenElement.toString());
            elements.add(element);
        }
        returnClass = gottenElements.get(0).getClass();
        isDistinct = gottenIsDistinct;
        functions = new Function[]{first, second};
        numberOfObjects = -1;
        isUnion = false;
        isJoin = true;
    }

    @SafeVarargs
    public SelectStmt(final List<R> gottenPastElements, final List<T> gottenElements, final Class<R> gottenReturnClass,
                      final boolean gottenIsDistinct, final Function<T, ?>... gottenFunctions) {
        elements = new ArrayList<>();
        for (T element : gottenElements) {
            elements.add(element);
        }
        returnClass = gottenReturnClass;
        isDistinct = gottenIsDistinct;
        functions = gottenFunctions;
        numberOfObjects = -1;
        isUnion = true;
        pastElements = gottenPastElements;
    }

    public SelectStmt(final List<R> gottenPastElements, final List<T> gottenElements, final boolean gottenIsDistinct,
                      final Function<T, ?> first, final Function<T, ?> second) {
        elements = new ArrayList<>();
        for (T element : gottenElements) {
            //System.out.println(element.toString());
            elements.add(element);
        }
        returnClass = gottenElements.get(0).getClass();
        isDistinct = gottenIsDistinct;
        functions = new Function[]{first, second};
        numberOfObjects = -1;
        isUnion = true;
        isJoin = true;
        pastElements = gottenPastElements;
    }

    public final SelectStmt<T, R> where(final Predicate<T> predicate) {
        whereCondition = predicate;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(final Function<T, ?>... expressions) {
        groupByConditions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(final Comparator<R>... gottenComparators) {
        comparators = gottenComparators;
        cqlComparator = new CQLComparator<R>(comparators);
        return this;
    }

    public final SelectStmt<T, R> having(final Predicate<R> condition) {
        havingCondition = condition;
        return this;
    }

    public final SelectStmt<T, R> limit(final int amount) {
        numberOfObjects = amount;
        return this;
    }

    public final Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        List<R> result = new ArrayList<>();
        Object[] arguments = new Object[functions.length];
        Class[] returnClasses = new Class[functions.length];
        if (whereCondition != null) {
            List<T> filtered = new ArrayList<>();
            elements.stream().filter(whereCondition::test).forEach(filtered::add);
            elements = filtered;
        }
        if (groupByConditions != null) {
            Map<Integer, Integer> mapped = new HashMap<>();
            String[] results = new String[groupByConditions.length];
            List<Pair<T, Integer>> grouped = new ArrayList<>();
            elements.stream().forEach(
                    element -> {
                        for (int i = 0; i < groupByConditions.length; i++) {
                            results[i] = (String) groupByConditions[i].apply(element);
                        }
                        if (!mapped.containsKey(Objects.hash(results))) {
                            mapped.put(Objects.hash(results), mapped.size());
                        }
                        grouped.add(new Pair(element, mapped.get(Objects.hash(results))));
                    }
            );
            List<List<T>> groupedElements = new ArrayList<>();
            for (int i = 0; i < mapped.size(); i++) {
                groupedElements.add(new ArrayList<T>());
            }

            for (Pair<T, Integer> element : grouped) {
                groupedElements.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : groupedElements) {
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
        if (havingCondition != null) {
            List<R> filtered = new ArrayList<>();
            result.stream().filter(havingCondition::test).forEach(filtered::add);
            result = filtered;
        }
        if (isDistinct) {
            Set<Integer> hashes = new HashSet<>();
            List<Integer> flags = new ArrayList<>();
            for (R element : result) {
                if (!hashes.contains(element.toString().hashCode())) {
                    flags.add(1);
                    hashes.add(element.toString().hashCode());
                } else {
                    flags.add(0);
                }
            }
            List<R> distincted = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                if (flags.get(i) == 1) {
                    distincted.add(result.get(i));
                }
            }
            result = distincted;
        }
        if (comparators != null) {
            result.sort(cqlComparator);
        }
        if (numberOfObjects != -1) {
            while (result.size() > numberOfObjects) {
                result.remove(result.size() - 1);
            }
        }
        if (isUnion) {
            pastElements.addAll(result);
            result = pastElements;
        }
        //System.out.println("Hello!");
        return result;
    }

    public final UnionStmt<T, R> union() throws InvocationTargetException, NoSuchMethodException,
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
        public CQLComparator(final Comparator<K>... gottenComparators) {
            comparators = gottenComparators;
        }

        @Override
        public final  int compare(final K first, final K second) {
            for (Comparator<K> comparator : comparators) {
                if (comparator.compare(first, second) != 0) {
                    return comparator.compare(first, second);
                }
            }
            return 0;
        }
    }

}
