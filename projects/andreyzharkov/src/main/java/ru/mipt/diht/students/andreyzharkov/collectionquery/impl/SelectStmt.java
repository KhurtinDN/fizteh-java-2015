package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SelectStmt<T, R> implements Query<R> {

    @SafeVarargs
    public SelectStmt(Function<T, R>... s) {
        throw new UnsupportedOperationException();
    }

    public WhereStmt<T, R> where(Predicate<T> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<R> execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

}