package ru.mipt.diht.students.alokotok.collectionquery.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lokotochek on 30.11.15.
 */
public class FromStmt<T> {
    public final List<T> getElements() {
        return elements;
    }

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
    public final <R> SelectStmt<T, R> select(Class<R> resultClass, Function<T, ?>... functions) {
        // 3-й аргумент: isDistinct
        return new SelectStmt<>(elements, resultClass, false, functions);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> resultClass, Function<T, ?>... functions) {
        // 3-й аргумент: isDistinct
        return new SelectStmt<>(elements, resultClass, true, functions);
    }

    public final <F, S> SelectStmt<T, Tuple<String, String>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(elements, false, first, second);
    }

    public final <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<T, J>(elements, iterable);
    }

    public class JoinClause<S, J> {

        private List<S> firstElements = new ArrayList<>();
        private List<J> secondElements = new ArrayList<>();
        private List<Tuple<S, J>> elements = new ArrayList<>();

        public JoinClause(List<S> firstElements, Iterable<J> secondElements) {
            this.firstElements.addAll(firstElements.stream().collect(Collectors.toList()));
            for (J element : secondElements) {
                this.secondElements.add(element);
            }
        }

        public final FromStmt<Tuple<S, J>> on(BiPredicate<S, J> condition) {
            for (S first : firstElements) {
                for (J second : secondElements) {
                    if (condition.test(first, second)) {
                        elements.add(new Tuple<>(first, second));
                    }
                }
            }
            return new FromStmt<>(elements);
        }


    }
}
