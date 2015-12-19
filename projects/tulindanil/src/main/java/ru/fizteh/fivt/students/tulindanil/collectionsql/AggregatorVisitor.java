package ru.fizteh.fivt.students.tulindanil.collectionsql;

/**
 * Created by tulindanil on 20.10.15.
 */


public interface AggregatorVisitor<T, R> {
    R result();
    void visit(T item);
}
