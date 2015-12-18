/*package ru.mipt.diht.students.lenazherdeva.CQL.impl;

/**
 * Created by admin on 18.11.2015.


import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SelectStmt<T, R> {

    private boolean isDistinct;
    private Class returnClazz;
    private Function[] functions;
    private List<T> objects;

    public boolean isDistinct() {
        return isDistinct;
    }
    public Class getReturnClass() {
        return returnClazz;
    }
    public Function[] getFunctions() {
        return functions;
    }

    public List<T> getElements() {
        return objects;
    }
    private Predicate<T> whereCondition;
    private Comparator<R>[] comparators;
    private Predicate<R> havingCondition;
    private int numberOfObjects;
    private Function<T, ?>[] groupByConditions;
    private CQLComparator<R> cqlComparator;









    public WhereStmt<T, R> where(Predicate<T> predicate) {
        this.whereCondition = predicate;
        return this;
    }




@Override
    public Iterable<R> execute() {
        throw new UnsupportedOperationException();
    }

    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }

    public class WhereStmt<T, R> {
        @SafeVarargs
        public final WhereStmt<T, R> groupBy(Function<T, ?>... expressions) {
            this.groupByConditions = expressions;
            return this;
        }

        @SafeVarargs
        public final WhereStmt<T, R> orderBy(Comparator<T>... comparators) {
            this.comparators = comparators;
            this.cqlComparator = new CQLComparator<R>(comparators);
            return this
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

}*/

