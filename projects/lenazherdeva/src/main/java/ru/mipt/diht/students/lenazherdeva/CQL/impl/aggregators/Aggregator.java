package ru.mipt.diht.students.lenazherdeva.CQL.impl.aggregators;

import java.util.List;
import java.util.function.Function;

/**
 * Created by admin on 17.11.2015.
*/
public interface Aggregator<T, E> extends Function<T, E> {
    E applyOnList(List<T> list);
}



