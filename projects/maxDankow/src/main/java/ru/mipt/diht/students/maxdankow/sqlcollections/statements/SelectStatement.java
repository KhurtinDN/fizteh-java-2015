package ru.mipt.diht.students.maxdankow.sqlcollections.statements;

import javafx.util.Pair;
import ru.mipt.diht.students.maxdankow.sqlcollections.aggregator.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStatement<T, R> {
    private List<R> previousItems;
    private List<T> items;
    private boolean shouldBeDistinct;
    private Class resultClass;
    private Function<T, ?>[] constructorExpressions;
    private Function<T, ?>[] groupByExpressions;
    private Comparator<R>[] orderByComparators;
    private ItemsComparator<R> comparator;
    private Predicate<T> whereCondition;
    private Predicate<R> havingCondition;
    private boolean isUnited;
    private boolean isJoined;
    private int limit = Integer.MAX_VALUE;

    @SafeVarargs
    public SelectStatement(List<T> newItems,
                           boolean newDistinct,
                           Class<R> newResultClass,
                           Function<T, ?>... s) {
        items = new ArrayList<>();
        for (T item : newItems) {
            items.add(item);
        }
        this.shouldBeDistinct = newDistinct;
        this.resultClass = newResultClass;
        constructorExpressions = s;
        isUnited = false;
        isJoined = false;
        limit = -1;
    }

    public SelectStatement(List<T> newItems,
                           boolean newDistinct,
                           Function<T, ?> first,
                           Function<T, ?> second) {
        items = new ArrayList<>();
        for (T item : newItems) {
            items.add(item);
        }
        resultClass = newItems.get(0).getClass();
        shouldBeDistinct = newDistinct;
        constructorExpressions = new Function[]{first, second};
        isUnited = false;
        isJoined = true;
        limit = -1;
    }

    @SafeVarargs
    public SelectStatement(List<R> newPreviousItems,
                           List<T> newItems,
                           boolean newDistinct, Class<R> newResultClass,
                           Function<T, ?>... newFunctions) {
        items = new ArrayList<>();
        previousItems = newPreviousItems;

        for (T element : newItems) {
            items.add(element);
        }

        resultClass = newResultClass;
        shouldBeDistinct = newDistinct;
        constructorExpressions = newFunctions;
        isUnited = true;
        limit = -1;
    }

    public final SelectStatement<T, R> where(Predicate<T> predicate) {
        whereCondition = predicate;
        return this;
    }

    public SelectStatement<T, R> having(Predicate<R> condition) {
        havingCondition = condition;
        return this;
    }

    public final SelectStatement<T, R> limit(int amount) {
        this.limit = amount;
        return this;
    }

    public final UnionStatement union() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<R> result = (List<R>) this.execute();
        return new UnionStatement(result);
    }

    @SafeVarargs
    public final SelectStatement<T, R> groupBy(Function<T, ?>... expressions) {
        groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStatement<T, R> orderBy(Comparator<R>... comparators) {
        orderByComparators = comparators;
        comparator = new ItemsComparator<>(comparators);
        return this;
    }

    public final Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<R> result = new ArrayList<>();
        Object[] constructorArgs = new Object[constructorExpressions.length];
        Class[] returnClasses = new Class[constructorExpressions.length];
        if (whereCondition != null) {
            List<T> afterWhere = new ArrayList<>();
            items.stream().filter(whereCondition::test).forEach(afterWhere::add);
            items = afterWhere;
        }

        if (groupByExpressions != null) {

            Map<Integer, Integer> map = new HashMap<>();

            String[] results = new String[groupByExpressions.length];

            List<Pair<T, Integer>> afterGroupBy = new ArrayList<>();

            items.stream().forEach((item) -> {
                        for (int i = 0; i < groupByExpressions.length; i++) {
                            results[i] = (String) groupByExpressions[i].apply(item);
                        }

                        if (!map.containsKey(Objects.hash(results))) {
                            map.put(Objects.hash(results), map.size());
                        }

                        afterGroupBy.add(new Pair(item, map.get(Objects.hash(results))));
                    }
            );

            List<List<T>> groupedElements = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                groupedElements.add(new ArrayList<T>());
            }

            for (Pair<T, Integer> element : afterGroupBy) {
                groupedElements.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : groupedElements) {
                for (int i = 0; i < constructorExpressions.length; i++) {
                    if (constructorExpressions[i] instanceof Aggregator) {
                        constructorArgs[i] = ((Aggregator) constructorExpressions[i]).apply(group);
                    } else {
                        constructorArgs[i] = constructorExpressions[i].apply(group.get(0));
                    }
                    returnClasses[i] = constructorArgs[i].getClass();
                }
                if (isJoined) {
                    Pair newElement = new Pair(constructorArgs[0], constructorArgs[1]);
                    result.add((R) newElement);
                } /*else {
                    //R newElement = (R) returnClass.getConstructor(returnClasses).newInstance(arguments);
                    //result.add(newElement);
                }*/

            }

            // Применяем having.
            if (havingCondition != null) {
                List<R> afterHaving = new ArrayList<>();
                result.stream().filter(havingCondition::test).forEach(afterHaving::add);
                result = afterHaving;
            }
        } else {
            for (T element : this.items) {
                for (int i = 0; i < constructorExpressions.length; i++) {
                    constructorArgs[i] = constructorExpressions[i].apply(element);
                    if (constructorExpressions[i] instanceof Aggregator) {
                        List<T> currArg = new ArrayList<>();
                        currArg.add(element);
                        constructorArgs[i] = ((Aggregator) constructorExpressions[i]).apply(currArg);
                    } else {
                        constructorArgs[i] = constructorExpressions[i].apply(element);
                    }
                    returnClasses[i] = constructorArgs[i].getClass();
                }
                if (isJoined) {
                    Pair newElement = new Pair(constructorArgs[0], constructorArgs[1]);
                    result.add((R) newElement);
                } else {
                    R newElement = (R) resultClass.getConstructor(returnClasses).newInstance(constructorArgs);
                    result.add(newElement);
                }
            }
        }

        // Если нужно, убираем поворяющиеся элементы.
        if (shouldBeDistinct) {
            Set<R> distinctItems = new HashSet<>();
            List<R> afterDistinct = new ArrayList<>();
            for (R element : result) {
                if (!distinctItems.contains(element)) {
                    distinctItems.add(element);
                    afterDistinct.add(element);
                }
            }
            result = afterDistinct;
        }

        // OrderBy - упорядочим.
        if (comparator != null) {
            result.sort(comparator);
        }

        // Ограничиваем по limit.
        if (limit != -1) {
            result = result.subList(0, Integer.min(result.size(), limit));
        }

        // Если select был вызван из union, то объединим результат с тем, что было.
        if (isUnited) {
            previousItems.addAll(result);
            result = previousItems;
        }
        return result;
    }
}
