package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public class FromStmt<T> extends CommonJoiner<T> {
    FromStmt(Stream<T> data) {
        super(data);
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(Utils.iterableToStream(iterable));
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(clazz, data, false, null, s);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(clazz, data, true, null, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> select(Function<T, R> s) {
        return new SelectStmt<>(null, data, false, null, s);
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
    public final <F, S> SelectStmt<T, Pair<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(null, data, false, null, first, second);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        return new SelectStmt<>(null, data, true, null, s);
    }

    public <J> FromStmtJoinClause<T, J> join(Iterable<J> iterable) {
        return new FromStmtJoinClause<>(super.join(iterable));
    }

    public <J> FromStmtJoinClause<T, J> join(Stream<J> stream) {
        return new FromStmtJoinClause<>(super.join(stream));
    }

    public <J> FromStmtJoinClause<T, J> join(Query<J> stream) {
        return new FromStmtJoinClause<>(super.join(stream));
    }

    public class FromStmtJoinClause<I, J> extends CommonJoinClause<I, J>
            implements JoinClause<FromStmt<Pair<I, J>>, I, J> {
        FromStmtJoinClause(CommonJoinClause<I, J> common) {
            super(common);
        }

        public FromStmt<Pair<I, J>> on(BiPredicate<I, J> condition) {
            return new FromStmt<>(onImpl(condition));
        }

        public <K extends Comparable<?>> FromStmt<Pair<I, J>> on(
                Function<I, K> leftKey,
                Function<J, K> rightKey) {
            return new FromStmt<>(onImpl(leftKey, rightKey));
        }
    }

}
