package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.List;
import java.util.stream.Stream;

public class UnionStmt<T, R> {

    private SelectStmt<T, R> parent;

    public UnionStmt(SelectStmt<T, R> rcvParent) {
        this.parent = rcvParent;
    }

    public final FromStmt<T> from(Iterable<T> list) {
        return new FromStmt<>(list, this);
    }

    public final FromStmt<T> from(Stream<T> list) {
        return new FromStmt<>(list, this);
    }

    public final List<R> execute() throws CqlException {
        return parent.executeGetList();
    }
}
