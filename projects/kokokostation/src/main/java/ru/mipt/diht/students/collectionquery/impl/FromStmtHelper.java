package ru.mipt.diht.students.collectionquery.impl;

import javafx.util.Pair;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmtHelper<T, R> extends CommonJoiner<T>{
    Context<R> context;

    FromStmtHelper(Stream<T> data, Context<R> context) {
        super(data);
        this.context = context;
    }

    public FromStmtHelper<T, R> from(Iterable<T> iterable) {
        return new FromStmtHelper<>(Utils.iterableToStream(iterable), null);
    }

    public FromStmtHelper<T, R> from(Stream<T> stream) {
        return new FromStmtHelper<>(stream, null);
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
        return new SelectStmt<>(null, data, true, context, s);
    }

    public <J> FromStmtHelperJoinClause<T, J> join(Iterable<J> iterable) {
        return new FromStmtHelperJoinClause<>(super.join(iterable));
    }

    public <J> FromStmtHelperJoinClause<T, J> join(Stream<J> stream) {
        return new FromStmtHelperJoinClause<>(super.join(stream));
    }

    public <J> FromStmtHelperJoinClause<T, J> join(Query<J> stream) {
        return new FromStmtHelperJoinClause<>(super.join(stream));
    }

    public class FromStmtHelperJoinClause<I, J> extends CommonJoinClause<I, J>
            implements JoinClause<FromStmtHelper<Pair<I, J>, R>, I, J> {
        FromStmtHelperJoinClause(CommonJoinClause<I, J> common) {
            super(common);
        }

        public FromStmtHelper<Pair<I, J>, R> on(BiPredicate<I, J> condition) {
            return new FromStmtHelper<>(onImpl(condition), context);
        }

        public <K extends Comparable<?>> FromStmtHelper<Pair<I, J>, R> on(
                Function<I, K> leftKey,
                Function<J, K> rightKey) {
            return new FromStmtHelper<>(onImpl(leftKey, rightKey), context);
        }
    }
}