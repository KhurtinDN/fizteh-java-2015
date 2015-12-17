package ru.mipt.diht.students.maxDankow.sqlcollections.statements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectStatement<T, R> {
    private List<R> previousItems;
    private List<T> items;
    private boolean shouldBeDistinct;
    private Class<R> resultClass;
    private Function<T, ?>[] constructorExpressions;
    private Function<T, ?>[] groupByExpressions;
    private Comparator<R>[] orderByComparators;
    private ItemsComparator<R> comparator;
    private Predicate<T> whereCondition;
    private Predicate<R> havingCondition;
    private boolean isUnited;
    private int limit = Integer.MAX_VALUE;

    @SafeVarargs
    public SelectStatement(List<T> newItems,
                           boolean newDistinct,
                           Class<R> newResultClass,
                           Function<T, ?>... s) {
        this.items = newItems;
        this.shouldBeDistinct = newDistinct;
        this.resultClass = newResultClass;
        constructorExpressions = s;
    }

    @SafeVarargs
    public SelectStatement(List<R> newPreviousItems,
                           List<T> newItems,
                           boolean newDistinct, Class<R> newResultClass,
                           Function<T, ?>... newFunctions) {
        items = new ArrayList<>();
        previousItems = newPreviousItems;

        for (T element : newItems) {
            items.add(element);
        }

        resultClass = newResultClass;
        shouldBeDistinct = newDistinct;
        constructorExpressions = newFunctions;
        isUnited = true;
        limit = -1;
    }

    public final SelectStatement<T, R> where(Predicate<T> predicate) {
        whereCondition = predicate;
        return this;
    }

    public SelectStatement<T, R> having(Predicate<R> condition) {
        havingCondition = condition;
        return this;
    }

    public final SelectStatement<T, R> limit(int amount) {
        this.limit = amount;
        return this;
    }

    public final UnionStatement union() {
        List<R> result = (List<R>) this.execute();
        return new UnionStatement(result);
    }

    @SafeVarargs
    public final SelectStatement<T, R> groupBy(Function<T, ?>... expressions) {
        groupByExpressions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStatement<T, R> orderBy(Comparator<R>... comparators) {
        orderByComparators = comparators;
        comparator = new ItemsComparator<>(comparators);
        return this;
    }

    public final Iterable<R> execute() {
        // todo: здесь главная запара.
        throw new UnsupportedOperationException();
    }
}
