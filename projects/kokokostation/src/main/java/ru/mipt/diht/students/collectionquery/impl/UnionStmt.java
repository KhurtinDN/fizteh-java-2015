package ru.mipt.diht.students.collectionquery.impl;

public class UnionStmt<R> {
    Context<R> context;

    public UnionStmt(Context<R> context) {
        this.context = context;
    }

    public <T> FromStmtHelper<T, R> from(Iterable<T> iterable) {
        return new FromStmtHelper<>(Utils.iterableToStream(iterable), context);
    }
}