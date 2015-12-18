package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.LinkedList;
import java.util.stream.Stream;

public class UnionStmt<R> {

    private SelectStmt<?, R> parent;

    public UnionStmt(SelectStmt<?, R> rcvParent) {
        this.parent = rcvParent;
    }

    public final <T> FromStmt<T> from(Iterable<T> list) {
        return new FromStmt<T>(list, this);
    }

    public final <T> FromStmt<T> from(Stream<T> list) {
        return new FromStmt<T>(list, this);
    }

    public final <T> FromStmt<T> from(Query query) {
        return new FromStmt<T>(query, this);
    }

    public final LinkedList<R> execute() throws CqlException {
        return parent.executeGetLinkedList();
    }
}
