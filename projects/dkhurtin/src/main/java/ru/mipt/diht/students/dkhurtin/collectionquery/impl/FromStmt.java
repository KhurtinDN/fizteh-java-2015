package ru.mipt.diht.students.dkhurtin.collectionquery.impl;

import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmt<T> {
    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        throw new UnsupportedOperationException();
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        throw new UnsupportedOperationException();
    }
}
