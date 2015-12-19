package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import ru.mipt.diht.students.ale3otik.collectionquery.aggregatesimpl.Aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectStmt<T, R> implements Query {
    private List<T> data;
    private Query<T> query;
    private boolean isDistinct;
    private Class<?> returnedClass;
    private Function<T, ?>[] functions;
    private Predicate<T> wherePredicate;
    private Function<T, ?>[] groupByExpressions;
    private Comparator<R> orderByComparator;
    private Predicate<R> havingCondition;
    private int limitRows = -1;
    private UnionStmt<?> parentUnion;
    private JoinClause<?, ?> joinClause;


    @SafeVarargs
    public SelectStmt(List<T> rcvData,
                      Query<T> rcvQuery,
                      Class<R> rcvReturnedClass,
                      boolean rcvIsDistinct,
                      UnionStmt<?> rcvParentUnion,
                      Function<T, ?>... rcvFunctions) {
        this.data = rcvData;
        this.isDistinct = rcvIsDistinct;
        this.returnedClass = rcvReturnedClass;
        this.parentUnion = rcvParentUnion;
        this.functions = rcvFunctions;
        this.query = rcvQuery;
    }

    public <F, S> SelectStmt(JoinClause<?, ?> rcvJoinClause,
                             UnionStmt<?> rcvParentUnion, Function<T, ?>... rcvFunctions) {
        this.joinClause = rcvJoinClause;
        this.parentUnion = rcvParentUnion;
        this.functions = rcvFunctions;
    }

    public final SelectStmt<T, R> where(Predicate<T> predicate) {
        this.wherePredicate = predicate;
        return this;
    }

    @Override
    public final Iterable<R> execute() throws CqlException {
        return executeGetLinkedList();
    }

    private List<T> applyWhere(List<T> source) {
        if (wherePredicate != null) {
            source = source.stream().filter(wherePredicate::test).collect(Collectors.toList());
        }
        return source;
    }

    private List<R> applyHaving(List<R> source) {
        if (havingCondition != null) {
            source = source.stream().filter(havingCondition::test).collect(Collectors.toList());
        }
        return source;
    }

    private List<R> applyOrderBy(List<R> source) {
        if (orderByComparator != null) {
            source.sort(orderByComparator);
        }
        return source;
    }

    private List<R> applyDistinct(List<R> source) {
        if (isDistinct) {
            source.stream().distinct().collect(Collectors.toList());
        }
        return source;
    }

    private List<R> applyLimit(List<R> source) {
        if (limitRows >= 0) {

            source = source.subList(0, Integer.min(limitRows, source.size()));
        }
        return source;
    }

    private List<List<T>> buildGroups(List<T> source) {
        List<List<T>> grouped = new ArrayList<>();
        if (groupByExpressions != null) {
            Map<List<Object>, Integer> buckets = new HashMap<>();
            List<Object> results = new ArrayList<>();
            source.stream().forEach(
                    element -> {
                        for (int i = 0; i < groupByExpressions.length; ++i) {
                            results.add(groupByExpressions[i].apply(element));
                        }
                        if (!buckets.containsKey(results)) {
                            int ind = buckets.size();
                            buckets.put(results, buckets.size());
                            grouped.add(new ArrayList<>());
                            grouped.get(ind).add(element);
                        } else {
                            int ind = buckets.get(results);
                            grouped.get(ind).add(element);
                        }
                    }
            );
        } else {
            source.stream().forEach(e -> {
                List<T> tmp = new ArrayList<T>();
                tmp.add(e);
                grouped.add(tmp);
            });
        }
        return grouped;
    }

    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    private List<R> getResultList(List<List<T>> buckets) throws CqlException {
        List<R> result = new ArrayList<>();

        Class[] resultClasses = new Class[functions.length];
        Object[] args = new Object[functions.length];
        try {
            for (List<T> group : buckets) {
                for (int i = 0; i < functions.length; ++i) {
                    Function f = functions[i];
                    args[i] = ((f instanceof Aggregator) ? ((Aggregator) f).apply(group) : f.apply(group.get(0)));
                    resultClasses[i] = args[i].getClass();
                }

                R record;
                if (joinClause != null) {
                    record = (R) (new Tuple<>(args[0], args[1]));
                } else {
                    if (returnedClass == null) {
                        record = (R) args[0];
                    } else {
                        record = (R) returnedClass.getConstructor(resultClasses).newInstance(args);
                    }
                }
                result.add(record);
            }
        } catch (NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException e) {
            throw new CqlException(e.getClass().toString() + " in getResultList " + e.getMessage());
        }
        return result;
    }

    private List<R> buildResultList() throws CqlException {
        if (data == null) {
            if (joinClause != null) {
                data = (List<T>) joinClause.excuteGetTupleList();
            } else {
                data = new ArrayList<>();
                Iterable<T> res = this.query.execute();
                res.forEach(e -> data.add(e));
            }
        }
        data = applyWhere(data);
        List<List<T>> grouped = buildGroups(data);
        List<R> result = getResultList(grouped);

        result = applyLimit(
                applyOrderBy(
                        applyDistinct(
                                applyHaving(result))));
        return result;
    }

    public final LinkedList<R> executeGetLinkedList() throws CqlException {
        LinkedList<R> previous;
        List<R> current = buildResultList();
        LinkedList<R> currentLinkedList = new LinkedList<>(current);
        if (parentUnion != null) {
            previous = (LinkedList<R>) parentUnion.execute();
            previous.addAll(currentLinkedList);
            currentLinkedList = previous;
        }
        return currentLinkedList;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) throws CqlException {
        if (groupByExpressions != null) {
            throw new CqlException("group by statement already used");
        }
        this.groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) throws CqlException {
        if (orderByComparator != null) {
            throw new CqlException("orderby statement already used");
        }
        this.orderByComparator = new GeneralComparator<>(comparators);
        return this;
    }

    public final SelectStmt<T, R> having(Predicate<R> condition) throws CqlException {
        if (havingCondition != null) {
            throw new CqlException("having statement already used");
        }
        this.havingCondition = condition;
        return this;
    }

    public final SelectStmt<T, R> limit(int amount) throws CqlException {
        assert amount >= 0;
        if (limitRows >= 0) {
            throw new CqlException("limit's already set");
        }

        this.limitRows = amount;
        return this;
    }

    public final UnionStmt<R> union() {
        return new UnionStmt<>(this);
    }
}
