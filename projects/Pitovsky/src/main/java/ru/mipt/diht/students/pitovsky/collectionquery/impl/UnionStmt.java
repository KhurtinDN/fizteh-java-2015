package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.util.Collection;

public class UnionStmt<R> {
    private Collection<R> previousPart;

    UnionStmt(SelectStmt<?, R> previousStmt) throws CollectionQueryExecuteException {
        previousPart = previousStmt.execute();
    }

    public final <T> FromStmt<T> from(Iterable<T> list) {
        throw new UnsupportedOperationException();
    }
}
