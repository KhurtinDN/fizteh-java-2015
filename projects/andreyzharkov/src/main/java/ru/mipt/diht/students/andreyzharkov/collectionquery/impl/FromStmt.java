package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FromStmt<T> {
    private Set<T> collection;

    FromStmt(Iterable<T> iterable) {
        for (T element : iterable) {
            collection.add(element);
        }
    }

    FromStmt(Stream<T> stream) {
        collection = stream.collect(Collectors.toSet());
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
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
