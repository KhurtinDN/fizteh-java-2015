package ru.mipt.diht.students.ale3otik.collectionquery.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by alex on 18.12.15.
 */
public class JoinClause<T, J> {
    private Query<T> firstDataQuery;
    private List<T> firstData;
    private Query<J> secondDataQuery;
    private List<J> secondData;
    private BiPredicate<T, J> condition;
    private Function<T, ?> leftKey;
    private Function<J, ?> rightKey;
    private UnionStmt<?> parentUnion;

    public JoinClause(UnionStmt<?> union, List<T> first, Query<T> query, Iterable<J> iterable) {
        parentUnion = union;
        firstData = first;
        firstDataQuery = query;
        secondData = new ArrayList<>();
        iterable.forEach(e -> secondData.add(e));
    }

    public JoinClause(UnionStmt<?> union, List<T> first, Query<T> query, Stream<J> stream) {
        parentUnion = union;
        firstData = first;
        firstDataQuery = query;
        secondData = new ArrayList<>();
        stream.forEach(e -> secondData.add(e));
    }

    public JoinClause(UnionStmt<?> union, List<T> first, Query<T> query, Query<J> scdQuery) {
        parentUnion = union;
        firstData = first;
        firstDataQuery = query;
        secondDataQuery = scdQuery;
    }

    public final FromStmt<Tuple<T, J>> on(BiPredicate<T, J> rcvCondition) {
        condition = rcvCondition;
        return new FromStmt<>(this, parentUnion);
    }

    public final <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
            Function<T, K> rcvLeftKey,
            Function<J, K> rcvRightKey) {
        leftKey = rcvLeftKey;
        rightKey = rcvRightKey;
        return new FromStmt<>(this, parentUnion);
    }

    public final List<Tuple<T, J>> excuteGetTupleList() throws CqlException {
        List<Tuple<T, J>> result = new ArrayList<>();
        if (firstData == null) {
            firstData = new ArrayList<>();
            firstDataQuery.execute().forEach(e -> firstData.add(e));
        }
        if (secondData == null) {
            secondData = new ArrayList<>();
            secondDataQuery.execute().forEach(e -> secondData.add(e));
        }

        if (condition != null) {
            firstData.forEach(first ->
                    secondData.forEach(second -> {
                        if (condition.test(first, second)) {
                            result.add(new Tuple<>(first, second));
                        }
                    }));
        } else {
            HashMap<Object, Object> map = new HashMap<>();
            firstData.forEach(e -> map.put(leftKey.apply(e), e));
            secondData.forEach(e -> {
                T l = (T) map.get(rightKey.apply(e));
                if (l != null) {
                    result.add(new Tuple<T, J>(l, e));
                }
            });
        }
        return result;
    }
}
