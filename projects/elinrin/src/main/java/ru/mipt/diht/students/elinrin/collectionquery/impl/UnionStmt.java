package ru.mipt.diht.students.elinrin.collectionquery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnionStmt<T, R> {

    private List<R> pastElements = new ArrayList<>();

    private boolean isTuple;

    public UnionStmt(final Iterable<R> iterable) {
        for (R curr : iterable) {
            pastElements.add(curr);
        }
        isTuple = false;
    }

    public UnionStmt(final Iterable<R> iterable, final boolean gottenIsTuple) {
        for (R curr : iterable) {
            pastElements.add(curr);
        }
        isTuple = true;
    }

    public final <S> FromClause<S, R> from(final Iterable<S> elements) {
        if (isTuple) {
            return new FromClause<>(pastElements, elements);
        } else {
            return new FromClause<>(pastElements, elements);
        }
    }


    public class FromClause<S, Q> {
        private List<Q> pastElements = new ArrayList<>();

        private List<S> elements = new ArrayList<>();

        public FromClause(final Iterable<Q> gottenPastElements, final Iterable<S> gottenElements) {
            for (Q curr : gottenPastElements) {
                pastElements.add(curr);
            }
            for (S curr : gottenElements) {
                elements.add(curr);
            }
        }
        @SafeVarargs
        public final SelectStmt<S, Q> select(final Class<Q> returnClass, final Function<S, ?>... functions) {
            return new SelectStmt<>(pastElements, elements, returnClass, false, functions);
        }

        public final <F, Z> SelectStmt<S, Tuple<F, Z>> select(final Function<S, F> first, final Function<S, Z> second) {
            return new SelectStmt<>((List<Tuple<F, Z>>) pastElements, elements, false, first, second);
        }

        @SafeVarargs
        public final SelectStmt<S, Q> selectDistinct(final Class<Q> returnClass, final Function<S, ?>... functions) {
            return new SelectStmt<>(pastElements, elements, returnClass, true, functions);
        }

        public final <J> JoinClause<Q, S, J> join(final Iterable<J> iterable) {
            return new JoinClause<>(pastElements, elements, iterable);
        }
    }


    public class JoinClause<W, F, J> {

        private List<F> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<W> pastElements = new ArrayList<>();
        private List<Tuple<F, J>> elements = new ArrayList<>();

        public JoinClause(final List<W> gottenPastElements, final List<F> gottenFirstElements,
                          final Iterable<J> gottenSecondElements) {
            pastElements.addAll(gottenPastElements.stream().collect(Collectors.toList()));
            firstElements.addAll(gottenFirstElements.stream().collect(Collectors.toList()));
            for (J curr : gottenSecondElements) {
                secondElements.add(curr);
            }
            //secondElements.forEach(System.out::print);
        }

        public final FromClause<Tuple<F, J>, W> on(final BiPredicate<F, J> condition) {
            for (F first : firstElements) {
                for (J second : secondElements) {
                    if (condition.test(first, second)) {
                        elements.add(new Tuple<>(first, second));
                    }
                }
            }
            //System.out.println(secondElements.get(0));
            //System.out.println(elements.get(0));
            return new FromClause<>(pastElements, elements);
        }

        public final <K extends Comparable<?>> FromClause<Tuple<F, J>, W> on(
                final Function<F, K> leftKey,
                final Function<J, K> rightKey) {
            HashMap<K, List<J>> map = new HashMap<>();
            for (J element : secondElements) {
                K key = rightKey.apply(element);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(element);
            }
            for (F first : firstElements) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    List<J> second = map.get(key);
                    second.forEach(s -> elements.add(new Tuple<>(first, s)));
                }
            }
            return new FromClause<>(pastElements, elements);
        }
    }
}
