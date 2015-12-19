package ru.mipt.diht.students.annnvl.CQL.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public interface Query<R> {

    Iterable<R> execute() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException;

    Stream<R> stream();
}

