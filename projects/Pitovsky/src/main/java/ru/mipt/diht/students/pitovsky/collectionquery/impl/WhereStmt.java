package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class WhereStmt<T, R> {
    private SelectStmt<T, R> baseStmt;

    WhereStmt(SelectStmt<T, R> selectStmt) { //it must be package-visible only
        baseStmt = selectStmt;
    }

    @SafeVarargs
    public final WhereStmt<T, R> groupBy(Function<T, Comparable<?>>... expressions)
            throws CollectionQuerySyntaxException {
        baseStmt.groupBy(expressions);
        return this;
    }

    @SafeVarargs
    public final WhereStmt<T, R> orderBy(Comparator<R>... comparators) throws CollectionQuerySyntaxException {
        baseStmt.orderBy(comparators);
        return this;
    }

    public final WhereStmt<T, R> having(Predicate<R> condition) throws CollectionQuerySyntaxException {
        baseStmt.having(condition);
        return this;
    }

    public final WhereStmt<T, R> limit(int amount) throws CollectionQuerySyntaxException {
        baseStmt.limit(amount);
        return this;
    }

    public final Iterable<R> execute() throws CollectionQueryExecuteException {
        return baseStmt.execute();
    }

    public final UnionStmt<R> union() throws CollectionQueryExecuteException {
        return baseStmt.union();
    }
}
