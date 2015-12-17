package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmt<T> {
    private List<T> data = new ArrayList<>();
    private UnionStmt<T, ?> parentUnion;

    private FromStmt(Iterable<T> iterable) {
        iterable.forEach(e -> data.add(e));
    }

    private FromStmt(Stream<T> stream) {
        stream.forEach(e -> data.add(e));
    }

    public <R> FromStmt(Iterable<T> iterable, UnionStmt<T, R> rcvParentUnion) {
        this.parentUnion = rcvParentUnion;
        iterable.forEach(e -> data.add(e));
    }

    public <R> FromStmt(Stream<T> stream, UnionStmt<T, R> rcvParentUnion) {
        this.parentUnion = rcvParentUnion;
        stream.forEach(e -> data.add(e));
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> returnedClass, Function<T, ?>... f) {
        return new SelectStmt<>(data, returnedClass, false, (UnionStmt<T, R>) parentUnion, f);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> returnedClass, Function<T, ?>... f) {
        return new SelectStmt<T, R>(data, returnedClass, true, (UnionStmt<T, R>) parentUnion, f);
    }
}
