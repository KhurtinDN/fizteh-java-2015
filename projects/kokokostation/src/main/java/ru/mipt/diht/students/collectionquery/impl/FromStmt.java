package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;
import ru.mipt.diht.students.collectionquery.AggregateFunction;
import ru.mipt.diht.students.collectionquery.CollectionQuery;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmt<T, R> extends Joiner<T, FromStmt.FromStmtJoinClause, FromStmt.FromStmtJoinClauseFactory> {
    UnionStmt<R> context;

    FromStmt(Stream<T> data, UnionStmt<R> context) {
        super(data);
        this.context = context;
        super.setFactory(new FromStmtJoinClauseFactory());
    }

    public FromStmt<T, R> from(Iterable<T> iterable) {
        return new FromStmt<>(Utils.iterableToStream(iterable), null);
    }

    public FromStmt<T, R> from(Stream<T> stream) {
        return new FromStmt<>(stream, null);
    }

    @SafeVarargs
    public final SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(clazz, data, false, context, s);
    }

    @SafeVarargs
    public final SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(clazz, data, true, context, s);
    }

    public final SelectStmt<T, R> select(Function<T, R> s) {
        return new SelectStmt<>(null, data, false, context, s);
    }

    public final SelectStmt<T, R> select(Function<T, ?> first, Function<T, ?> second) {
        return new SelectStmt<>(null, data, false, context, first, second);
    }

    public final SelectStmt<T, R> selectDistinct(Function<T, R> s) {
        return new SelectStmt<T, R>(null, data, true, context, s);
    }

    public class FromStmtJoinClause<I, J> extends CommonJoinClause<I, J> {
        public FromStmtJoinClause(List<I> left, List<J> right) {
            super(left, right);
        }

        public FromStmt<Pair<I, J>, R> on(BiPredicate<I, J> condition) {
            return new FromStmt<>(super.commonOn(condition), context);
        }

        public <K extends Comparable<?>> FromStmt<Pair<I, J>, R> on(
                Function<I, K> leftKey,
                Function<J, K> rightKey) {
            return new FromStmt<>(super.commonOn(leftKey, rightKey), context);
        }
    }

    public class FromStmtJoinClauseFactory implements JoinClauseFactory<FromStmtJoinClause> {
        @Override
        public <I, J> FromStmtJoinClause<I, J> produce(List<I> left, List<J> right) {
            return new FromStmtJoinClause<>(left, right);
        }
    }
}