package ru.mipt.diht.students.ale3otik.collectionquery.impl;

/**
 * Created by alex on 18.12.15.
 */
public interface Query<R> {
    Iterable<R> execute() throws CqlException;
}
