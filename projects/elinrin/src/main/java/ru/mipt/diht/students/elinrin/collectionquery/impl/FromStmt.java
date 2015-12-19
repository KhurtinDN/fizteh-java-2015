package ru.mipt.diht.students.elinrin.collectionquery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FromStmt<T> {
    private List<T> elements = new ArrayList<>();

    public final List<T> getElements() {
        return elements;
    }

    public FromStmt(final Iterable<T> iterable) {
        for (T curr : iterable) {
            elements.add(curr);
        }
    }

    public static <T> FromStmt<T> from(final Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(final Class<R> returnClass, final Function<T, ?>... functions) {
        return new SelectStmt<>(elements, returnClass, false, functions);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(final Class<R> returnClass,
                                                     final Function<T, ?>... functions) {
        return new SelectStmt<>(elements, returnClass, true, functions);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(final Function<T, F> first,
                                                          final Function<T, S> second) {
        return new SelectStmt<>(elements, false, first, second);
    }

    public final <J> JoinClause<T, J> join(final Iterable<J> iterable) {
        return new JoinClause<>(elements, iterable);
    }

    public class JoinClause<S, J> {

        private List<S> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<Tuple<S, J>> elements = new ArrayList<>();

        public JoinClause(final List<S> firstElements, final Iterable<J> secondElements) {
            this.firstElements.addAll(firstElements.stream().collect(Collectors.toList()));
            for (J curr : secondElements) {
                this.secondElements.add(curr);
            }
        }

        public final FromStmt<Tuple<S, J>> on(final BiPredicate<S, J> condition) {
            for (S first : firstElements) {
                for (J second : secondElements) {
                    if (condition.test(first, second)) {
                        elements.add(new Tuple<>(first, second));
                    }
                }
            }
            return new FromStmt<>(elements);
        }

        public final <K extends Comparable<?>> FromStmt<Tuple<S, J>> on(
                final Function<S, K> leftKey,
                final Function<J, K> rightKey) {
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
}
