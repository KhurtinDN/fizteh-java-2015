package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class UnionStmt<R> {
    private Collection<R> previousPart;

    UnionStmt(SelectStmt<?, R> previousStmt) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        previousPart = previousStmt.execute();
    }

    public final <T> FromStmt<T> from(Iterable<T> list) {
        throw new UnsupportedOperationException();
    }
}
