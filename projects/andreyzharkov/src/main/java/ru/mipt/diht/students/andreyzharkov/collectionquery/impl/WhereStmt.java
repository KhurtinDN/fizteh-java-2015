package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by Андрей on 08.12.2015.
 */
public class WhereStmt<T, R> implements Query<R> {
    @SafeVarargs
    public final WhereStmt<T, R> groupBy(Function<T, ?>... expressions) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
        throw new UnsupportedOperationException();
    }

    public WhereStmt<T, R> having(Predicate<R> condition) {
        throw new UnsupportedOperationException();
    }

    public WhereStmt<T, R> limit(int amount) {
        throw new UnsupportedOperationException();
    }

    public UnionStmt union() {
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
