package ru.fizteh.fivt.students.chipak.collectionquery.impl;

import java.util.List;

/**
 * Created by kormushin on 09.10.15.
 */
public class UnionStmt<R> {
    private List<R> previousResult;

    public <T> FromStmt<T> from(Iterable<T> list) {
        FromStmt<T> newFrom =  new FromStmt<>(list);
        newFrom.setPreviousResult(previousResult);
        return newFrom;
    }

    public UnionStmt(List<R> previousResult) {
        this.previousResult = previousResult;
    }


}