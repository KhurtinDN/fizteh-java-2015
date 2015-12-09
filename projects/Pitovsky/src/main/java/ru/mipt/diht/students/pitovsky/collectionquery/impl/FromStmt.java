package ru.mipt.diht.students.pitovsky.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class FromStmt<T> {
    private Iterable<T> base;

    private FromStmt() { }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        FromStmt<T> stmt = new FromStmt<>();
        stmt.base = iterable;
        return stmt;
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        FromStmt<T> stmt = new FromStmt<>();
        List<T> list = new ArrayList<>();
        stream.forEach(s -> list.add(s));
        stmt.base = list;
        return stmt;
    }

    public static <T> FromStmt<T> from(WhereStmt<?, T> subStmt) throws CollectionQueryExecuteException {
        Iterable<T> subQueryResult = subStmt.execute();
        return from(subQueryResult);
    }

    /**
     * Get rows, which applies for conditions.
     * @param clazz class, which able to store. Must have constructor from functions returns types (see below)
     * @param s parameters and aggregates for output
     * @return select statement for execution
     */
    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<T, R>(base, clazz, false, s);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<T, R>(base, clazz, true, s);
    }
}