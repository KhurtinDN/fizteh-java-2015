package ru.mipt.diht.students.simon23rus.CQL.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnionStmt<T, R> {

    private List<R> oldQueries = new ArrayList<>();
    private List<T> currentQuery = new ArrayList<>();
    private List<Tuple<T, R>> oldTupleElements = new ArrayList<>();
    private  boolean isTuple;
    public UnionStmt(Iterable<R> toIterate) {
        toIterate.forEach(elem -> oldQueries.add(elem));
    }
    public UnionStmt(Iterable<R> toIterate, boolean isTuple) {
        toIterate.forEach(elem -> oldQueries.add(elem));
        this.isTuple = true;
    }

    public UnionStmt(Iterable<T> current, Iterable<R> old) {
        old.forEach(elem -> oldQueries.add(elem));
        current.forEach(elem -> currentQuery.add(elem));
    }

    public <S> FromMember<S, R> from(Iterable<S> elements) {
        if (isTuple) {
            return new FromMember<S, R>(oldQueries, elements);
        } else {
            return new FromMember<S, R>(oldQueries,  elements);
        }
    }


    public class FromMember<S, R> {
        public List<R> getOldElements() {
            return oldElements;
        }
        private List<R> oldElements = new ArrayList<>();
        private List<S> currentElements = new ArrayList<>();

        public List<S> getCurrentElements() {
            return currentElements;
        }

        public FromMember(Iterable<R> pastElements, Iterable<S> elements) {
            pastElements.forEach(elem -> this.oldElements.add(elem));
            elements.forEach(elem -> this.currentElements.add(elem));
        }
        @SafeVarargs
        public final SelectStmt<S, R> select(Class<R> returnClass, Function<S, ?>... functions) {
            return new SelectStmt<S, R>((List<R>) oldElements, currentElements, returnClass, false, functions);
        }

        public final <F, Z> SelectStmt<S, Tuple<F, Z>> select(Function<S, F> first, Function<S, Z> second) {
            return new SelectStmt<S, Tuple<F, Z>>((List<Tuple<F, Z>>) oldElements, currentElements, false, first, second);
        }

        @SafeVarargs
        public final SelectStmt<S, R> selectDistinct(Class<R> returnClass, Function<S, ?>... functions) {
            return new SelectStmt<S, R>((List<R>) oldElements, currentElements, returnClass, true, functions);
        }

        public <J> JoinMember<R, S, J> join(Iterable<J> iterable) {
            return new JoinMember<R, S, J>(oldElements, currentElements, iterable);
        }
    }

    public class JoinMember<R, F, J> {

        private List<F> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<R> oldElements = new ArrayList<>();
        private List<Tuple<F, J>> currentElements = new ArrayList<>();

        public JoinMember(List<R> pastElements, List<F> firstElements, Iterable<J> secondElements) {
            this.oldElements.addAll(pastElements.stream().collect(Collectors.toList()));
            this.firstElements.addAll(firstElements.stream().collect(Collectors.toList()));
            secondElements.forEach(elem -> this.secondElements.add(elem));
        }

        public FromMember<Tuple<F, J>, R> on(BiPredicate<F, J> condition) {
            firstElements.forEach(first ->
                    secondElements.forEach(second -> {
                        //esli udovletvoryaet predikatu, to ostavlyaem
                        if (condition.test(first, second)) {
                            this.currentElements.add(new Tuple<>(first, second));
                        }
                    }));
            return new FromMember<>(oldElements, currentElements);
        }

        public <K extends Comparable<?>> FromStmt<Tuple<F, J>> on(
                Function<F, K> leftKey,
                Function<J, K> rightKey) {
            throw new UnsupportedOperationException();
        }
    }
}


