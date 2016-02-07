package ru.mipt.diht.students.collectionquery.impl;

import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public class CommonJoiner<T> {
    protected final Stream<T> data;

    CommonJoiner(Stream<T> data) {
        this.data = data;
    }

    public <J> CommonJoinClause<T, J> join(Iterable<J> iterable) {
        return new CommonJoinClause<T, J>(Utils.streamToList(data), Utils.iterableToList(iterable));
    }

    public <J> CommonJoinClause<T, J> join(Stream<J> stream) {
        return new CommonJoinClause<>(Utils.streamToList(data), Utils.streamToList(stream));
    }

    public <J> CommonJoinClause<T, J> join(Query<J> stream) {
        return join(stream.execute());
    }
}
