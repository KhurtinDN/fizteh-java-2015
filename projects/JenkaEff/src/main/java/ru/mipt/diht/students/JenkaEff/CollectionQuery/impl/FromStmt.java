package ru.fizteh.fivt.students.JenkaEff.CollectionQuery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FromStmt<T> {
    private List<T> list = new ArrayList<T>();

    public FromStmt(Iterable<T> iterable) {
        for (T curr : iterable) {
            list.add(curr);
        }
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... functions) {
        return new SelectStmt<>(list, clazz, false, functions);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... functions) {
        return new SelectStmt<>(list, clazz, true, functions);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(list, false, first, second);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<T, J>(list, iterable);
    }

    public class JoinClause<S, J> {
        private List<S> firstList = new ArrayList<>();
        private List<J> secondList = new ArrayList<>();
        private List<Tuple<S, J>> list = new ArrayList<>();

        public JoinClause(List<S> firstList, Iterable<J> secondList) {
            this.firstList.addAll(firstList.stream().collect(Collectors.toList()));
            for (J curr : secondList) {
                this.secondList.add(curr);
            }
        }

        public FromStmt<Tuple<S, J>> on(BiPredicate<S, J> condition) {
            for (S first : firstList) {
                list.addAll(secondList.stream().filter(second -> condition.test(first, second))
                        .map(second -> new Tuple<>(first, second)).collect(Collectors.toList()));
            }
            return new FromStmt<>(list);
        }

        public <K extends Comparable<?>> FromStmt<Tuple<S, J>> on(
                Function<S, K> leftKey,
                Function<J, K> rightKey) {
            HashMap<K, List<J>> map = new HashMap<>();
            for (J e : secondList) {
                K key = rightKey.apply(e);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(e);
            }
            for (S first : firstList) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    List<J> second = map.get(key);
                    second.forEach(s -> list.add(new Tuple<>(first, s)));
                }
            }
            return new FromStmt<>(list);
        }
    }
}
