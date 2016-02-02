package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public class FromStmtInit<T> extends Joiner<T, FromStmtInit.FromStmtInitJoinClause, FromStmtInit.FromStmtInitJoinClauseFactory> {
    FromStmtInit(Stream<T> data) {
        super(data);
        super.setFactory(new FromStmtInitJoinClauseFactory());
    }

    public static <T> FromStmtInit<T> from(Iterable<T> iterable) {
        return new FromStmtInit<>(Utils.iterableToStream(iterable));
    }

    public static <T> FromStmtInit<T> from(Stream<T> stream) {
        return new FromStmtInit<>(stream);
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
        return new SelectStmt<T, R>(null, data, true, null, s);
    }

    public class FromStmtInitJoinClause<I, J> extends CommonJoinClause<I, J> {
        public FromStmtInitJoinClause(List<I> left, List<J> right) {
            super(left, right);
        }

        public FromStmtInit<Pair<I, J>> on(BiPredicate<I, J> condition) {
            return new FromStmtInit<>(super.commonOn(condition));
        }

        public <K extends Comparable<?>> FromStmtInit<Pair<I, J>> on(
                Function<I, K> leftKey,
                Function<J, K> rightKey) {
            return new FromStmtInit<>(super.commonOn(leftKey, rightKey));
        }
    }

    public class FromStmtInitJoinClauseFactory implements JoinClauseFactory<FromStmtInitJoinClause> {
        @Override
        public <I, J> FromStmtInitJoinClause<I, J> produce(List<I> left, List<J> right) {
            return new FromStmtInitJoinClause<>(left, right);
        }
    }
}
