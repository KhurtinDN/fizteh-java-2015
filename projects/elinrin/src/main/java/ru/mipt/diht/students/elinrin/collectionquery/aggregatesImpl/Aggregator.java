package ru.mipt.diht.students.elinrin.collectionquery.aggregatesImpl;

import java.util.List;
import java.util.function.Function;


public interface Aggregator<T, C> extends Function<T, C> {
    C apply(List<T> elements);
}
