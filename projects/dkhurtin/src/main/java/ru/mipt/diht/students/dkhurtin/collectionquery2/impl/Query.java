package ru.mipt.diht.students.dkhurtin.collectionquery2.impl;

import java.util.stream.Stream;

public interface Query<R> {

    Iterable<R> execute();

    Stream<R> stream();
}
