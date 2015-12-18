package ru.mipt.diht.students.andreyzharkov.collectionquery.impl;

import java.util.stream.Stream;

/**
 * Created by Андрей on 14.12.2015.
 */

public interface Query<R> {

    Iterable<R> execute() throws QueryExecuteException, EmptyCollectionException;

    Stream<R> stream() throws QueryExecuteException, EmptyCollectionException;
}