package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;
import ru.mipt.diht.students.collectionquery.AggregateFunction;
import ru.mipt.diht.students.collectionquery.AggregateFunctionImplementation;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SelectStmt<T, R> {
    private static final long UNLIMITED = -1;
    private final Class<R> clazz;
    private final Function<T, ?>[] selectors;
    private final boolean isDistinct;
    private Context<R> context;
    private Stream<T> data;
    private Predicate<T> wherePredicate;
    private Function<T, ?>[] groupByExpressions;
    private Predicate<R> havingPredicate;
    private Comparator<List<T>> orderByComparator;
    private long limit = UNLIMITED;

    @SafeVarargs
    SelectStmt(Class<R> clazz, Stream<T> data, boolean isDistinct, Context<R> context, Function<T, ?>... selectors) {
        this.isDistinct = isDistinct;
        this.data = data;
        this.clazz = clazz;
        this.context = context;
        this.selectors = selectors;
    }

    public SelectStmt<T, R> where(Predicate<T> predicate) {
        wherePredicate = predicate;

        return this;
    }

    public Iterable<R> execute() throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        List<R> result = new ArrayList<>();

        if (context != null) {
            for (SelectStmt<?, R> select : context.get()) {
                result.addAll(select.executeStmt());
            }
        }

        result.addAll(executeStmt());

        return result;
    }

    private List<R> executeStmt() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (wherePredicate != null) {
            data = data.filter(wherePredicate);
        }

        Stream<List<T>> finalGroups;

        if (groupByExpressions != null) {
            finalGroups = groupBy();
        } else {
            finalGroups = data.map(Arrays::asList);
        }

        if (orderByComparator != null) {
            finalGroups = finalGroups.sorted(orderByComparator);
        }
        if (isDistinct) {
            finalGroups = finalGroups.distinct();
        }
        if (limit != UNLIMITED) {
            finalGroups = finalGroups.limit(limit);
        }

        List<R> result = new ArrayList<>();
        for (List<T> item : Utils.streamToList(finalGroups)) {
            result.add(makeObject(item));
        }

        return result;
    }

    private Stream<List<T>> groupBy() throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        List<List<T>> groups = new ArrayList<>();
        groups.add(new ArrayList<>());
        data.forEach(groups.get(0)::add);

        for (Function<T, ?> expression : groupByExpressions) {
            List<List<T>> newGroups = new ArrayList<>();
            HashMap<Pair<Integer, ?>, Integer> hashMap = new HashMap<>();

            for (int i = 0; i < groups.size(); i++) {
                for (T item : groups.get(i)) {
                    Pair<Integer, ?> key = new Pair<>(i, expression.apply(item));

                    if (!hashMap.containsKey(key)) {
                        hashMap.put(key, newGroups.size());

                        newGroups.add(Utils.arrayListFromElement(item));
                    } else {
                        newGroups.get(hashMap.get(key)).add(item);
                    }
                }
            }

            groups = newGroups;
        }

        if (havingPredicate != null) {
            List<List<T>> newGroups = new ArrayList<>();
            for (List<T> group : groups) {
                if (havingPredicate.test(makeObject(group))) {
                    newGroups.add(group);
                }
            }

            groups = newGroups;
        }

        return groups.stream();
    }

    private R makeObject(List<T> item) throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (clazz != null) {
            return constructObject(item);
        } else if (selectors.length == 1) {
            return getObject(item);
        } else if (selectors.length == 2) {
            return getPair(item);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private R constructObject(List<T> item) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] arguments = new Object[selectors.length];
        Class[] argumentsClasses = new Class[selectors.length];

        for (int i = 0; i < selectors.length; i++) {
            arguments[i] = applySelector(selectors[i], item);
            argumentsClasses[i] = arguments[i].getClass();
        }

        return clazz.getConstructor(argumentsClasses).newInstance(arguments);
    }

    @SuppressWarnings ("unchecked")
    private R getObject(List<T> item) {
        return (R) applySelector(selectors[0], item);
    }

    @SuppressWarnings ("unchecked")
    private R getPair(List<T> item) {
        return (R) new Pair<>(applySelector(selectors[0], item), applySelector(selectors[1], item));
    }

    @SuppressWarnings ("unchecked")
    private Object applySelector(Function<T, ?> selector, List<T> item) {
        if (selector.getClass() == AggregateFunctionImplementation.class) {
            return ((AggregateFunction<T, ?>) selector).apply(item);
        } else {
            return selector.apply(item.get(0));
        }
    }

    public Stream<R> stream() throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        return Utils.iterableToStream(execute());
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
        groupByExpressions = expressions;

        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<List<T>>... comparators) {
        orderByComparator = comparators[0];
        for (int i = 1; i < comparators.length; i++) {
            orderByComparator.thenComparing(comparators[i]);
        }

        return this;
    }

    public SelectStmt<T, R> having(Predicate<R> condition) {
        havingPredicate = condition;

        return this;
    }

    public SelectStmt<T, R> limit(long amount) {
        limit = amount;

        return this;
    }

    public UnionStmt<R> union() {
        if (context == null) {
            context = new Context<>();
        }

        context.add(this);

        return new UnionStmt<>(context);
    }
}
