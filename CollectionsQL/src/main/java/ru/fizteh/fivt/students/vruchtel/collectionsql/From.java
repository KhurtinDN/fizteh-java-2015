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
public class From<T> {
    private List<T> elements = new ArrayList<>();

    public List<T> getElements() {
        return elements;
    }

    public From(Iterable<T> iterable) {
        for (T it : iterable) {
            elements.add(it);
        }

    }
    public static <T> From<T> from(Iterable<T> iterable) {
        return new From<>(iterable);
    }

    //public final <R> Select<T, R> select(Class<R> returnClass, Function<T, ?>... functions) {
      //  return new Select<>(elements, returnClass, false, functions);
    //}

    //public final <R> Select<T, R> selectDistinct(Class<R> returnClass, Function<T, ?>... functions) {
      //  return new Select<>(elements, returnClass, true, functions);
    //}

    //public final <F, S> Select<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
      //  return new Select<>(elements, false, first, second);
    //}

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<>(elements, iterable);
    }

    public class JoinClause<S, J> {
        private List<S> firstPart = new ArrayList<>();
        private List<J> secondPart = new ArrayList<>();
        private List<Tuple<S, J>> elements = new ArrayList<>();

        public JoinClause(List<S> _firstPart, Iterable<J> _secondPart) {
            firstPart.addAll(_firstPart.stream().collect(Collectors.toList()));
            for (J curr : _secondPart) {
                secondPart.add(curr);
            }
        }
        public From<Tuple<S, J>> on(BiPredicate<S, J> condition) {
            for (S first : firstPart) {
                for (J second : secondPart) {
                    if (condition.test(first, second)) {
                        //elements.add(new Tuple<>(first, second));
                    }
                }
            }
            return new From<>(elements);
        }

        public <K extends Comparable<?>> From<Tuple<S, J>> on(Function<S, K> leftKey, Function<J, K> rightKey) {
            HashMap<K, List<J>> map = new HashMap<>();
            for (J element : secondPart) {
                K key = rightKey.apply(element);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(element);
            }

            for (S first : firstPart) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    List<J> second = map.get(key);
                   // second.forEach(s -> elements.add(new Tuple<>(first, s)));
                }
            }
            return new From<>(elements);
        }
    }
}