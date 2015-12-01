package ru.mipt.diht.students.maxDankow.sqlcollections.impl;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStmt<T, R> {
    private Iterable<T> items;
    private boolean shouldBeDistinct;
    private Class<R> resultClass;
    private Function<T, ?>[] constructorExpressions;
    private Function<T, ?>[] groupByExpressions;
    private Comparator<R>[] orderByComparators;
    private Predicate<T> whereCondition;
    private Predicate<R> havingCondition;
    private int limit = Integer.MAX_VALUE;

    @SafeVarargs
    public SelectStmt(Function<T, ?>... s) {
        constructorExpressions = s;
    }

    public SelectStmt<T, R> where(Predicate<T> predicate) {
        whereCondition = predicate;
        return this;
    }

    public Iterable<R> execute() {
        // todo: здесь главная запара.
        throw new UnsupportedOperationException();
    }

    public SelectStmt<T, R> having(Predicate<R> condition) {
        havingCondition = condition;
        return this;
    }

    public SelectStmt<T, R> limit(int amount) {
        this.limit = amount;
        return this;
    }

    public UnionStmt union() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
        groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) {
        orderByComparators = comparators;
        return this;
    }
}
