package ru.mipt.diht.students.simon23rus.CQL.impl;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FromStmt<T> {

    private List<T> data = new ArrayList<T>();

    public List<T> getData() {
        return  data;
    }

    public FromStmt(Iterable<T> toIterate) {
        toIterate.forEach(e -> data.add(e));
    }

    public  FromStmt(Stream<T> myStream) {
        myStream.forEach(e -> data.add(e));
    }


    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return  new FromStmt<T>(stream);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, false, s);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return  new SelectStmt<>(data, clazz, true, s);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(data, false, first, second);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<T, J>(data, iterable);
    }

    public class JoinClause<S, J> {

        private List<T> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<Tuple<T, J>> joinedElements = new ArrayList<>();

        public JoinClause(List<T> firstElements, Iterable<J> secondElements) {
            this.firstElements
                    .addAll(firstElements.stream()
                    .collect(Collectors.toList()));
            for (J curr : secondElements) {
                this.secondElements.add(curr);
            }
        }

        public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
            firstElements.forEach(first ->
                    secondElements.forEach(second -> {
                        if (condition.test(first, second)) {
                            this.joinedElements.add(new Tuple<>(first, second));
                        }
                    }));
            return new FromStmt<>(joinedElements);
        }

        public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
                Function<T, K> leftKey,
                Function<J, K> rightKey) {
            Map<K, List<T>> leftMap = firstElements.stream().collect(Collectors.groupingBy(leftKey));
            Map<K, List<J>> rightMap = secondElements.stream().collect(Collectors.groupingBy(rightKey));
            leftMap.forEach((key, value) -> {
                if (rightMap.containsKey(key)) {
                    value.forEach(fst -> rightMap.get(key).forEach(snd -> joinedElements.add(new Tuple(fst, snd))));
                }
            });
            return new FromStmt<>(joinedElements);
        }
    }

}
