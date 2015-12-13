package ru.mipt.diht.students.maxDankow.sqlcollections.impl;

import java.util.function.Function;

public class FromStmt<T> {
    Iterable<T> items;

    public FromStmt(Iterable<T> iterable) {
        items = iterable;
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<T>(iterable);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<T, R>(items, false, clazz, s);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<T, R>(items, true, clazz, s);
    }
}
