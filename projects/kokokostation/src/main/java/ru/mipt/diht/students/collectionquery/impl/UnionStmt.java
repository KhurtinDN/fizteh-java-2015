package ru.mipt.diht.students.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;

public class UnionStmt<R> {
    List<SelectStmt<?, R>> selectStmts = new ArrayList<>();

    void add(SelectStmt<?, R> selectStmt) {
        selectStmts.add(selectStmt);
    }
    Iterable<SelectStmt<?, R>> get() {
        return selectStmts;
    }

    public <T> FromStmt<T, R> from(Iterable<T> iterable) {
        return new FromStmt<>(Utils.iterableToStream(iterable), this);
    }
}
