package ru.fizteh.fivt.students.JenkaEff.CollectionQuery.impl;

import ru.fizteh.fivt.students.JenkaEff.CollectionQuery.impl.Pair;
import ru.fizteh.fivt.students.JenkaEff.CollectionQuery.Aggregates;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectStmt<T, R>  {
    private boolean isDistinct;
    private Class clazz;
    private Function[] functions;
    private List<T> list;
    private List<R> pastList;
    private Predicate<T> wherePredicate;
    private Comparator<R>[] orderByComparators;
    private Predicate<R> havingPredicate;
    private int limit;
    private Function<T, ?>[] groupByFunctions;
    private CQLComparator<R> cqlComparator;
    private boolean isUnion;
    private boolean isJoin;

    private void init(List<T> list, Class clazz, boolean isDistinct, Function[] functions,
                      boolean isUnion, boolean isJoin, List<R> pastList) {
        this.list = new ArrayList<>();
        this.list.addAll(list.stream().collect(Collectors.toList()));
        this.clazz = clazz;
        this.isDistinct = isDistinct;
        this.functions = functions;
        this.limit = -1;
        this.isUnion = isUnion;
        this.isJoin = isJoin;
        this.pastList = pastList;
    }

    @SafeVarargs
    public SelectStmt(List<T> list, Class<R> clazz, boolean isDistinct, Function<T, ?>... functions) {
        init(list, clazz, isDistinct, functions, false, false, null);
    }

    public SelectStmt(List<T> list, boolean isDistinct, Function<T, ?> first, Function<T, ?> second) {
        init(list, list.get(0).getClass(), isDistinct,  new Function[]{first, second}, false, true, null);
    }

    @SafeVarargs
    public SelectStmt(List<R> pastList, List<T> list, Class<R> clazz, boolean isDistinct, Function<T, ?>... functions) {
        init(list, clazz, isDistinct, functions, true, false, pastList);
    }

    public SelectStmt(List<R> pastList, List<T> list, boolean isDistinct, Function<T, ?> first, Function<T, ?> second) {
        init(list, list.get(0).getClass(), isDistinct, new Function[]{first, second}, true, true, pastList);
    }

    public SelectStmt<T, R> where(Predicate<T> predicate) {
        this.wherePredicate = predicate;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... functions) {
        this.groupByFunctions = functions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... orderByComparators) {
        this.orderByComparators = orderByComparators;
        this.cqlComparator = new CQLComparator<R>(orderByComparators);
        return this;
    }

    public SelectStmt<T, R> having(Predicate<R> predicate) {
        this.havingPredicate = predicate;
        return this;
    }

    public SelectStmt<T, R> limit(int amount) {
        this.limit = amount;
        return this;
    }

    public Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        List<R> result = new ArrayList<>();
        Object[] arguments = new Object[functions.length];
        Class[] clazzes = new Class[functions.length];
        if (wherePredicate != null) {
            List<T> filtered = new ArrayList<>();
            list.stream().filter(wherePredicate::test).forEach(filtered::add);
            list = filtered;
        }
        if (groupByFunctions != null) {
            Map<Integer, Integer> mapped = new HashMap<>();
            String[] results = new String[groupByFunctions.length];
            List<Pair<T, Integer>> groupped = new ArrayList<>();
            list.stream().forEach(
                    element -> {
                        for (int i = 0; i < groupByFunctions.length; i++) {
                            results[i] = (String) groupByFunctions[i].apply(element);
                        }
                        if (!mapped.containsKey(Objects.hash(results))) {
                            mapped.put(Objects.hash(results), mapped.size());
                        }
                        groupped.add(new Pair(element, mapped.get(Objects.hash(results))));
                    }
            );
            List<List<T>> grouppedList = new ArrayList<>();
            for (int i = 0; i < mapped.size(); i++) {
                grouppedList.add(new ArrayList<T>());
            }

            for (Pair<T, Integer> element : groupped) {
                grouppedList.get(element.getValue()).add(element.getKey());
            }
            for (List<T> group : grouppedList) {
                for (int i = 0; i < functions.length; i++) {
                    if (functions[i] instanceof  Aggregates.Aggregator) {
                        arguments[i] = ((Aggregates.Aggregator) functions[i]).apply(group);
                    } else {
                        arguments[i] = functions[i].apply(group.get(0));
                    }
                    clazzes[i] = arguments[i].getClass();
                }
                if (isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    result.add((R) newElement);
                } else {
                    R newElement = (R) clazz.getConstructor(clazzes).newInstance(arguments);
                    result.add(newElement);
                }
            }
        } else {
            for (T element : this.list) {
                for (int i = 0; i < functions.length; i++) {
                    arguments[i] = functions[i].apply(element);
                    if (functions[i] instanceof  Aggregates.Aggregator) {
                        List<T> currArg = new ArrayList<>();
                        currArg.add(element);
                        arguments[i] = ((Aggregates.Aggregator) functions[i]).apply(currArg);
                    } else {
                        arguments[i] = functions[i].apply(element);
                    }
                    clazzes[i] = arguments[i].getClass();
                }
                if (isJoin) {
                    Tuple newElement = new Tuple(arguments[0], arguments[1]);
                    result.add((R) newElement);
                } else {
                    R newElement = (R) clazz.getConstructor(clazzes).newInstance(arguments);
                    result.add(newElement);
                }
            }
        }
        if (havingPredicate != null) {
            List<R> filtered = new ArrayList<>();
            result.stream().filter(havingPredicate::test).forEach(filtered::add);
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
        if (orderByComparators != null) {
            result.sort(cqlComparator);
        }
        if (limit != -1) {
            while (result.size() > limit) {
                result.remove(result.size() - 1);
            }
        }
        if (isUnion) {
            pastList.addAll(result);
            result = pastList;
        }
        return result;
    }

    public UnionStmt<R> union() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        List<R> result = (List<R>) this.execute();
        return new UnionStmt<>(result);
    }

    public class CQLComparator<K> implements Comparator<K> {
        private Comparator<K>[] orderByComparators;
        @SafeVarargs
        public CQLComparator(Comparator<K>... orderByComparators) {
            this.orderByComparators = orderByComparators;
        }

        @Override
        public int compare(K first, K second) {
            for (Comparator<K> comparator : orderByComparators) {
                if (comparator.compare(first, second) != 0) {
                    return comparator.compare(first, second);
                }
            }
            return 0;
        }
    }

}