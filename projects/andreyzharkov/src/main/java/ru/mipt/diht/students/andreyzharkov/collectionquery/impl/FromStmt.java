package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FromStmt<T> {
    private Iterable<?> previousQuery; //because of union
    private Iterable<T> data;

    FromStmt(Iterable<T> iterable) {
        this.data = iterable;
    }

    FromStmt(Iterable<T> iterable, Iterable<?> previous) {
        data = iterable;
        previousQuery = previous;
    }

    private FromStmt(Stream<T> stream) {
        data = stream.collect(Collectors.toList());
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    public static <T> FromStmt<T> from(Query<T> query) throws QueryExecuteException, EmptyCollectionException {
        return new FromStmt<>(query.execute());
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<R>) previousQuery, data, clazz, false, s);
        } catch (ClassCastException ex) {
            try {
                List<R> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((R) el));
                return new SelectStmt<>(newIterable, data, clazz, false, s);
            } catch (ClassCastException exeption) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> select(Function<T, R> s) throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<R>) previousQuery, data, false, s);
        } catch (ClassCastException ex) {
            try {
                List<R> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((R) el));
                return new SelectStmt<>(newIterable, data, false, s);
            } catch (ClassCastException exception) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param first
     * @param second
     * @param <F>
     * @param <S>
     * @return statement resulting in collection of R
     */
    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second)
            throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<Tuple<F, S>>) previousQuery, data, false, first, second);
        } catch (ClassCastException ex) {
            try {
                List<Tuple<F, S>> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((Tuple<F, S>) el));
                return new SelectStmt<>(newIterable, data, false, first, second);
            } catch (ClassCastException exception) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s)
            throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<R>) previousQuery, data, clazz, true, s);
        } catch (ClassCastException ex) {
            try {
                List<R> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((R) el));
                return new SelectStmt<>(newIterable, data, clazz, false, s);
            } catch (ClassCastException exception) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<R>) previousQuery, data, false, s);
        } catch (ClassCastException ex) {
            try {
                List<R> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((R) el));
                return new SelectStmt<>(newIterable, data, false, s);
            } catch (ClassCastException exception) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> selectDistinct(Function<T, F> first, Function<T, S> second)
            throws UnequalUnionClassesException {
        try {
            return new SelectStmt<>((Iterable<Tuple<F, S>>) previousQuery, data, false, first, second);
        } catch (ClassCastException ex) {
            try {
                List<Tuple<F, S>> newIterable = new ArrayList<>();
                previousQuery.forEach(el -> newIterable.add((Tuple<F, S>) el));
                return new SelectStmt<>(newIterable, data, false, first, second);
            } catch (ClassCastException exception) {
                throw new UnequalUnionClassesException("Uncasted classes can't be union.", ex);
            }
        }
    }

    public final <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<>(data, iterable);
    }

    public final <J> JoinClause<T, J> join(Stream<J> stream) {
        return new JoinClause<>(data, stream.collect(Collectors.toList()));
    }

    public final <J> JoinClause<T, J> join(Query<J> stream) throws QueryExecuteException, EmptyCollectionException {
        return new JoinClause<>(data, stream.execute());
    }

    public class JoinClause<T, J> {
        private Iterable<T> first;
        private Iterable<J> second;

        JoinClause(Iterable<T> fst, Iterable<J> snd) {
            this.first = fst;
            this.second = snd;
        }

        public final FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
            //не знаю, что тут лучше использовать, чтобы быстро удалять
            //элементы могут повторятся
            List<T> list = new LinkedList<>();
            first.forEach(list::add);

            List<Tuple<T, J>> result = new ArrayList<>();
            second.forEach(element -> {
                for (int i = 0; i < list.size(); i++) {
                    if (condition.test(list.get(i), element)) {
                        result.add(new Tuple<>(list.get(i), element));
                        list.remove(i);
                        break;
                    }
                }
            });

            return new FromStmt<>(result);
        }

        public final <K extends Comparable<K>> FromStmt<Tuple<T, J>> on(
                Function<T, K> leftKey,
                Function<J, K> rightKey) {

            Map<K, List<T>> mapOfT = new HashMap<>();
            first.forEach(elem -> {
                K key = leftKey.apply(elem);
                if (!mapOfT.containsKey(key)) {
                    List<T> inserted = new LinkedList<>();
                    inserted.add(elem);
                    mapOfT.put(key, inserted);
                } else {
                    List<T> inserted = mapOfT.get(key);
                    inserted.add(elem);
                    mapOfT.put(key, inserted);
                }
            });

            List<Tuple<T, J>> result = new ArrayList<>();
            second.forEach((elem) -> {
                K key = rightKey.apply(elem);
                if (mapOfT.containsKey(key)) {
                    List<T> replaced = mapOfT.get(key);
                    result.add(new Tuple<>(replaced.get(0), elem));
                    replaced.remove(0);
                    if (replaced.isEmpty()) {
                        mapOfT.remove(key);
                    } else {
                        mapOfT.put(key, replaced);
                    }
                }
            });

            return new FromStmt<>(result);
        }
    }
}
