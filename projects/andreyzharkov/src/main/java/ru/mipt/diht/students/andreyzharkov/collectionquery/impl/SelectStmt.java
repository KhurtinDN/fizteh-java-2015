package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SelectStmt<T, R> {
    Function<T, R>[] functions;
    Collection<T> collection;
    Class<R> returnedClass;
    boolean isDistinct;

    @SafeVarargs
    public SelectStmt(Collection<T> collection, boolean isDistinct, Class<R> returnedClass, Function<T, R>... s) {
        functions = s;
        this.returnedClass = returnedClass;
        this.collection = collection;
        this.isDistinct = isDistinct;
    }

    public WhereStmt<T, R> where(Predicate<T> predicate) {
        return new WhereStmt<T, R>(collection, isDistinct, returnedClass, predicate, functions);
    }

    public Iterable<R> execute() {
        throw new UnsupportedOperationException();
    }

    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

}
