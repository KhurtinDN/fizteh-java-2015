package ru.fizteh.fivt.students.chipak.collectionquery.impl;

import java.util.stream.Stream;


public interface Query<R> {

    Iterable<R> execute() throws ReflectiveOperationException;

    Stream<R> stream() throws ReflectiveOperationException;
}