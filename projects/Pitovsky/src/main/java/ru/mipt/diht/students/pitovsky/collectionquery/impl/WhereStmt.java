package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

public class WhereStmt<T, R> {
    private SelectStmt<T, R> baseStmt;

    WhereStmt(SelectStmt<T, R> selectStmt, Predicate<T> predicate) { //it must be package-visible only
        baseStmt = selectStmt;
        baseStmt.updateStream(baseStmt.currentStream().filter(predicate));
    }

    @SafeVarargs
    public final WhereStmt<T, R> groupBy(Function<T, Comparable<?>>... expressions) {
        baseStmt.setGroupingFunctions(expressions);
        return this;
    }

    static <T> Comparator<T> getCombinedComparator(Iterable<Comparator<T>> comparators) {
        return new Comparator<T>() {
            @Override
            public int compare(T first, T second) {
                for (Comparator<T> comparator : comparators) {
                    int result = comparator.compare(first, second);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }

    @SafeVarargs
    public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
        baseStmt.updateStream(baseStmt.currentStream().sorted(getCombinedComparator(Arrays.asList(comparators))));
        return this;
    }

    public WhereStmt<T, R> having(Predicate<R> condition) {
        baseStmt.setGroupingCondition(condition);
        return this;
    }

    public WhereStmt<T, R> limit(int amount) {
        baseStmt.updateStream(baseStmt.currentStream().limit(amount));
        return this;
    }

    public Iterable<R> execute() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return baseStmt.execute();
    }

    public UnionStmt union() {
        throw new UnsupportedOperationException();
    }
}
