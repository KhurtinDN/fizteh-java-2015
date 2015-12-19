package ru.mipt.diht.students.annnvl.CQL.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class FromStmt<T> {

    private List<T> elements = new ArrayList<T>();

    public FromStmt(Iterable<T> iterable) {
        for (T element : iterable) {
            elements.add(element);
        }
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStmt<>(elements, returnClass, false, functions);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> returnClass, Function<T, ?>... functions) {
        return new SelectStmt<>(elements, returnClass, true, functions);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(elements, false, first, second);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<T, J>(elements, iterable);
    }

    public class JoinClause<S, J> {


        private List<S> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<Tuple<S, J>> elements = new ArrayList<>();

        public JoinClause(List<S> firstElements, Iterable<J> secondElements) {
            this.firstElements.addAll(firstElements);
            secondElements.forEach(this.secondElements::add);
        }

        public FromStmt<Tuple<S, J>> on(BiPredicate<S, J> condition) {
            for (S first : firstElements) {
                for (J second : secondElements) {
                    if (condition.test(first, second)) {
                        elements.add(new Tuple<>(first, second));
                    }
                }
            }
            return new FromStmt<>(elements);
        }

        public <K extends Comparable<?>> FromStmt<Tuple<S, J>> on(
                Function<S, K> leftKey,
                Function<J, K> rightKey) {
            HashMap<K, List<J>> map = new HashMap<>();
            for (J element : secondElements) {
                K key = rightKey.apply(element);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(element);
            }
            for (S first : firstElements) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    List<J> second = map.get(key);
                    second.forEach(s -> elements.add(new Tuple<>(first, s)));
                }
            }
            return new FromStmt<>(elements);
        }
    }

    public List<T> getElements() {
        return elements;
    }
}


