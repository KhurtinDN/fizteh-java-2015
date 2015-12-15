package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FromStmt<T> {
    Iterable<T> data;

    FromStmt(Iterable<T> iterable) {
        this.data = iterable;
    }

    private FromStmt(Stream<T> stream) {
        data = stream.collect(Collectors.toList());
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    public static <T> FromStmt<T> from(Query<T> query) throws QueryExecuteException {
        return new FromStmt<>(query.execute());
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, false, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> select(Function<T, R> s) {
        return new SelectStmt<>(data, false, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param first
     * @param second
     * @param <F>
     * @param <S>
     * @return statement resulting in collection of R
     */
    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(data, false, first, second);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(data, clazz, true, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        return new SelectStmt<>(data, false, s);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        throw new UnsupportedOperationException();
    }

    public <J> JoinClause<T, J> join(Stream<J> stream) {
        throw new UnsupportedOperationException();
    }

    public <J> JoinClause<T, J> join(Query<J> stream) {
        throw new UnsupportedOperationException();
    }

    public class JoinClause<T, J> {

        public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
            throw new UnsupportedOperationException();
        }

        public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
                Function<T, K> leftKey,
                Function<J, K> rightKey) {
            throw new UnsupportedOperationException();
        }
    }
}
