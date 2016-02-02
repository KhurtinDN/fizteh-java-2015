package ru.mipt.diht.students.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by mikhail on 02.02.16.
 */
public class Joiner<T, C extends CommonJoinClause, F extends JoinClauseFactory<C>> {
    protected final Stream<T> data;
    private F factory;

    public Joiner(Stream<T> data) {
        this.data = data;
    }

    public void setFactory(F factory) {
        this.factory = factory;
    }

    public <J> C join(Iterable<J> iterable) {
        return factory.produce(Utils.streamToList(data), Utils.iterableToList(iterable));
    }

    public <J> C join(Stream<J> stream) {
        return factory.produce(Utils.streamToList(data), Utils.streamToList(stream));
    }

    public <J> C join(Query<J> stream) {
        return join(stream.execute());
    }
}
