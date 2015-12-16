package ru.mipt.diht.students.alokotok.collectionquery.impl;

import java.util.List;
import java.util.function.Function;

/**
 * Created by lokotochek on 30.11.15.
 */
// класс-интерфейс, от которого наследуются агрегаторы (avg, count, max, min)
public interface Aggregator<T, C> extends Function<T, C> {
    C apply(List<T> elements);
}