package ru.fizteh.fivt.students.JenkaEff.CollectionQuery.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnionStmt<R> {
    private List<R> pastList = new ArrayList<>();

    public UnionStmt(Iterable<R> iterable) {
        for (R curr : iterable) {
            pastList.add(curr);
        }
    }

    public <S> FromClause<S, R> from(Iterable<S> list) {
        return new FromClause<S, R>(pastList, list);
    }

    public class FromClause<S, R> {
        private List<R> pastList = new ArrayList<>();
        private List<S> list = new ArrayList<>();

        public FromClause(Iterable<R> pastList, Iterable<S> list) {
            for (R curr : pastList) {
                this.pastList.add(curr);
            }
            for (S curr : list) {
                this.list.add(curr);
            }
        }
        @SafeVarargs
        public final SelectStmt<S, R> select(Class<R> clazz, Function<S, ?>... functions) {
            return new SelectStmt<S, R>((List<R>) pastList, list, clazz, false, functions);
        }

        public final <F, Z> SelectStmt<S, Tuple<F, Z>> select(Function<S, F> first, Function<S, Z> second) {
            return new SelectStmt<S, Tuple<F, Z>>((List<Tuple<F, Z>>) pastList, list, false, first, second);
        }

        @SafeVarargs
        public final SelectStmt<S, R> selectDistinct(Class<R> clazz, Function<S, ?>... functions) {
            return new SelectStmt<S, R>((List<R>) pastList, list, clazz, true, functions);
        }

        public <J> JoinClause<R, S, J> join(Iterable<J> iterable) {
            return new JoinClause<R, S, J>(pastList, list, iterable);
        }
    }

    public class JoinClause<R, F, J> {

        private List<F> firstList = new ArrayList<>();
        private List<J> secondList = new ArrayList<>();
        private List<R> pastList = new ArrayList<>();
        private List<Tuple<F, J>> list = new ArrayList<>();

        public JoinClause(List<R> pastList, List<F> firstList, Iterable<J> secondList) {
            this.pastList.addAll(pastList.stream().collect(Collectors.toList()));
            this.firstList.addAll(firstList.stream().collect(Collectors.toList()));
            for (J curr : secondList) {
                this.secondList.add(curr);
            }
        }

        public FromClause<Tuple<F, J>, R> on(BiPredicate<F, J> condition) {
            for (F first : firstList) {
                for (J second : secondList) {
                    if (condition.test(first, second)) {
                        list.add(new Tuple<>(first, second));
                    }
                }
            }
            return new FromClause<>(pastList, list);
        }

        public <K extends Comparable<?>> FromClause<Tuple<F, J>, R> on(
                Function<F, K> leftKey,
                Function<J, K> rightKey) {
            HashMap<K, List<J>> map = new HashMap<>();
            for (J e : secondList) {
                K key = rightKey.apply(e);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(e);
            }
            for (F first : firstList) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    List<J> second = map.get(key);
                    second.forEach(s -> list.add(new Tuple<>(first, s)));
                }
            }
            return new FromClause<>(pastList, list);
        }
    }
}