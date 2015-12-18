package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmt<T> {
    private List<T> data = new ArrayList<>();
    private Query<T> query;
    private UnionStmt<?> parentUnion;
    private JoinClause<?, ?> joinClause;

    public FromStmt(Iterable<T> iterable) {
        iterable.forEach(e -> data.add(e));
    }

    public FromStmt(Stream<T> stream) {
        stream.forEach(e -> data.add(e));
    }

    public FromStmt(Query rcvQuery) {
        this.query = rcvQuery;
    }

    public FromStmt(JoinClause<?, ?> clause, UnionStmt<?> rcvParentUnion) {
        this.joinClause = clause;
        this.parentUnion = rcvParentUnion;
    }

    public <R> FromStmt(Iterable<T> iterable, UnionStmt<?> rcvParentUnion) {
        this.parentUnion = rcvParentUnion;
        iterable.forEach(e -> data.add(e));
    }

    public <R> FromStmt(Query rcvQuery, UnionStmt<?> rcvParentUnion) {
        this.parentUnion = rcvParentUnion;
        this.query = rcvQuery;
    }

    public <R> FromStmt(Stream<T> stream, UnionStmt<R> rcvParentUnion) {
        this.parentUnion = rcvParentUnion;
        stream.forEach(e -> data.add(e));
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> returnedClass, Function<T, ?>... f) {
        return new SelectStmt<>(data, query, returnedClass, false, (UnionStmt<R>) parentUnion, f);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> returnedClass, Function<T, ?>... f) {
        return new SelectStmt<>(data, query, returnedClass, true, (UnionStmt<R>) parentUnion, f);
    }

    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> f) {
        return new SelectStmt<>(data, query, null, true, (UnionStmt<R>) parentUnion, f);
    }

    public final <F, S> SelectStmt<T, Tuple<F, S>> select(
            Function<T, F> first,
            Function<T, S> second) {
        return new SelectStmt<T, Tuple<F, S>>(joinClause, parentUnion, first, second);
    }

    public final <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<T, J>(parentUnion, data, query, iterable);
    }

    public final <J> JoinClause<T, J> join(Stream<J> stream) {
        return new JoinClause<T, J>(parentUnion, data, query, stream);
    }

    public final <J> JoinClause<T, J> join(Query<J> rcvQuery) {
        return new JoinClause<T, J>(parentUnion, data, query, rcvQuery);
    }
}


