package ru.mipt.diht.students.collectionquery.impl;

import java.util.stream.Stream;

public interface Query<R> {

    Iterable<R> execute();

    Stream<R> stream();
}
