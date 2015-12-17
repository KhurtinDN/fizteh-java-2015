package ru.mipt.diht.students.maxDankow.sqlcollections.statements;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStatement<T, R> {
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
    public SelectStatement(Iterable<T> items,
                           boolean shouldBeDistinct,
                           Class<R> resultClass,
                           Function<T, ?>... s) {
        this.items = items;
        this.shouldBeDistinct = shouldBeDistinct;
        this.resultClass = resultClass;
        constructorExpressions = s;
    }

    public SelectStatement<T, R> where(Predicate<T> predicate) {
        whereCondition = predicate;
        return this;
    }

    public Iterable<R> execute() {
        // todo: здесь главная запара.
        throw new UnsupportedOperationException();
    }

    public SelectStatement<T, R> having(Predicate<R> condition) {
        havingCondition = condition;
        return this;
    }

    public SelectStatement<T, R> limit(int amount) {
        this.limit = amount;
        return this;
    }

    public UnionStatement union() {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public final SelectStatement<T, R> groupBy(Function<T, ?>... expressions) {
        groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStatement<T, R> orderBy(Comparator<R>... comparators) {
        orderByComparators = comparators;
        shouldBeDistinct = true;
        return this;
    }
}
