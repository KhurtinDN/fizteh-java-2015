package ru.mipt.diht.students.maxdankow.sqlcollections.aggregator;

import java.util.List;
import java.util.function.Function;

public interface Aggregator<T, R> extends Function<T, R> {
    R apply(List<T> t);
}
