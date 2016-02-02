package ru.mipt.diht.students.collectionquery;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by mikhail on 01.02.16.
 */
public interface AggregateFunction<C, T> extends Function<C, T> {
    T apply(List<C> item);
}
