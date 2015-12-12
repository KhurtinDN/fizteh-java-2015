package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Андрей on 08.12.2015.
 */
public class WhereStmt<T, R> {
    private Collection<T> collection;
    private Comparator<T>[] comparators;
    private Function<T, ?>[] groupingFunctions;
    private Predicate<T> predicate;
    private Class<R> returnedClass;
    private int amount;
    private boolean isDistinct;

    public WhereStmt(Collection<T> collection, boolean isDistinct, Class<R> returnedClass,
                     Predicate<T> predicate, Function<T, R>... s) {
        this.collection = collection;
        this.predicate = predicate;
        this.groupingFunctions = s;
        this.isDistinct = isDistinct;
        this.returnedClass = returnedClass;
    }

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
}
