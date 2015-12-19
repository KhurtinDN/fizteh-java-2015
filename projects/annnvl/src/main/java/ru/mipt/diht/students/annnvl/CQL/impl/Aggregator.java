package ru.mipt.diht.students.annnvl.CQL.impl;

import java.util.List;
import java.util.function.Function;

public interface Aggregator<T, R> extends Function<T, R> {
    R apply(List<T> elements);
}
