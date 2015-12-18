package ru.fizteh.fivt.students.vruchtel.collectionsql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Серафима on 18.12.2015.
 */
public class Union<T, R> {

    private List<R> previousElements = new ArrayList<>();

    private boolean isTuple;

    public Union(Iterable<R> iterable) {
        for (R curr : iterable) {
            previousElements.add(curr);
        }
        this.isTuple = false;
    }

    public Union(Iterable<R> iterable, boolean tuple) {
        for (R it : iterable) {
            previousElements.add(it);
        }
        isTuple = true;
    }

    public <S> FromClause<S, R> from(Iterable<S> elements) {
        if (isTuple) {
            return new FromClause<>(previousElements, elements);
        } else {
            return new FromClause<>(previousElements, /*(Iterable<T>)*/ elements);
        }
    }

    public class FromClause<S, Q> {
        private List<Q> previousElements = new ArrayList<>();

        private List<S> elements = new ArrayList<>();

        public FromClause(Iterable<Q> previousElements, Iterable<S> elements) {
            for (Q curr : previousElements) {
                this.previousElements.add(curr);
            }
            for (S curr : elements) {
                this.elements.add(curr);
            }
        }

        public final Select<S, Q> select(Class<Q> returnClass, Function<S, ?>... functions) throws Exception{
            return new Select<>(previousElements, elements, returnClass, false, functions);
        }

        public final <F, Z> Select<S, Tuple<F, Z>> select(Function<S, F> first, Function<S, Z> second)
                throws Exception {
            return new Select<>((List<Tuple<F, Z>>) previousElements, elements, false, first, second);
        }

        public final Select<S, Q> selectDistinct(Class<Q> returnClass, Function<S, ?>... functions) throws Exception {
            return new Select<>(previousElements, elements, returnClass, true, functions);
        }

        public <J> JoinClause<Q, S, J> join(Iterable<J> iterable) {
            return new JoinClause<>(previousElements, elements, iterable);
        }
    }

    public class JoinClause<W, F, J> {
        private List<F> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<W> previousElements = new ArrayList<>();
        private List<Tuple<F, J>> elements = new ArrayList<>();

        public JoinClause(List<W> previousElements, List<F> firstElements, Iterable<J> secondElements) {
            this.previousElements.addAll(previousElements.stream().collect(Collectors.toList()));
            this.firstElements.addAll(firstElements.stream().collect(Collectors.toList()));
            for (J curr : secondElements) {
                this.secondElements.add(curr);
            }
        }

        public FromClause<Tuple<F, J>, W> on(BiPredicate<F, J> condition) {
            for (F first : firstElements) {
                for (J second : secondElements) {
                    if (condition.test(first, second)) {
                        elements.add(new Tuple<>(first, second));
                    }
                }
            }
            return new FromClause<>(previousElements, elements);
        }

        public <K extends Comparable<?>> FromClause<Tuple<F, J>, W> on(Function<F, K> leftKey,
                                                                       Function<J, K> rightKey) {
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
            return new FromClause<>(previousElements, elements);
        }
    }
}